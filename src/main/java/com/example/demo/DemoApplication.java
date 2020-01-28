package com.example.demo;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class DemoApplication {

	@Value("${watcher.path}")
	private String path;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	@Profile("watcher")
	public void initWatcher() throws IOException {
		WatchService watcher = FileSystems.getDefault().newWatchService();
		Paths.get(path).register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

		new Thread(() -> {
			for (;;) {

				// wait for key to be signaled
				WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException x) {
					return;
				}

				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();

					// This key is registered only for ENTRY_CREATE events,
					// but an OVERFLOW event can occur regardless if events
					// are lost or discarded.
					if (kind == OVERFLOW) {
						continue;
					}

					// The filename is the context of the event.
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path filename = ev.context();

					System.out.println(kind + ":" + filename);
				}

				// Reset the key -- this step is critical if you want to
				// receive further watch events. If the key is no longer valid,
				// the directory is inaccessible so exit the loop.
				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		}).start();
	}
}
