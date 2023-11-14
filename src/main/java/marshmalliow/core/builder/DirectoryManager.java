package marshmalliow.core.builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marshmalliow.core.objects.Directory;

public class DirectoryManager {
	
	private static final Logger LOGGER = LogManager.getLogger(DirectoryManager.class);
	
	private final Object mutex = new Object();

	private Set<Directory> directories = new HashSet<>();
	
	public DirectoryManager() {
	}
	
	public boolean registerNewDirectory(Directory dir) {
		synchronized (mutex) {
			try {
				if(this.directories.add(dir)) {
					if(Files.createDirectories(dir.getPath()) != null) LOGGER.info("Directory "+dir.getName()+" has been successfully created!");
					else LOGGER.info("Directory "+dir.getName()+" has been successfully loaded!");
					
					return true;
				}
				return false;
			}catch(SecurityException | IOException e) {
				LOGGER.error("An error occured while creating directory",dir.getName(),". Skipping 1 directory.",e.getMessage());
				return false;
			}
		}
	}
	
	public boolean registerNewDirectoryIfAbsent(Directory dir) {
		synchronized (mutex) {
			if(!this.directories.contains(dir)) {
				try {
					if(this.directories.add(dir)) {
						if(Files.createDirectories(dir.getPath()) != null) LOGGER.info("Directory "+dir.getName()+" has been successfully created!");
						else LOGGER.info("Directory "+dir.getName()+" has been successfully loaded!");
						
						return true;
					}
					return false;
				}catch(SecurityException | IOException e) {
					LOGGER.error("An error occured while creating directory",dir.getName(),". Skipping 1 directory.",e.getMessage());
					return false;
				}
			}
			return false;
		}
	}
	
	public void registerNewDirectory(Directory... directories) {
		synchronized (mutex) {
			for (Directory dir : directories) {
				try {
					if(this.directories.add(dir)) {
						if(Files.createDirectories(dir.getPath()) != null) LOGGER.info("Directory "+dir.getName()+" has been successfully created!");
						else LOGGER.info("Directory "+dir.getName()+" has been successfully loaded!");
					}
				}catch(SecurityException | IOException e) {
					LOGGER.error("An error occured while creating directory",dir.getName(),". Skipping 1 directory.",e.getMessage());
				}
			}
		}
	}
	
	public boolean removeDirectory(Directory dir) {
		synchronized (mutex) {
			if(this.directories.remove(dir)) {
				LOGGER.info("Directory "+dir.getName()+" has been successfully unloaded!");
				return true;
			}
			
			return false;
		}
	}

	public Directory getLoadedDirectory(String nameId) {
		synchronized (mutex) {
			return this.directories.parallelStream().filter(dir -> dir.isNameSimilar(nameId)).findFirst().orElse(null);
		}
	}
	
	public Directory getLoadedDirectory(Path path) {
		synchronized (mutex) {
			return this.directories.parallelStream().filter(dir -> dir.isPathSimilar(path)).findFirst().orElse(null);
		}
	}
	
	public boolean exist(Directory dir) {
		synchronized (mutex) {
			return this.directories.contains(dir);
		}
	}
	
	/**
	 * Get the total of stored directories
	 * @return the size of the directories's list
	 */
	public int getDirectoriesNumber() {
		return this.directories.size();
	}
}