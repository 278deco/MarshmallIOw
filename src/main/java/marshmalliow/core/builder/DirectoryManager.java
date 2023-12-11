package marshmalliow.core.builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marshmalliow.core.objects.Directory;

public class DirectoryManager {
	
	private static final Logger LOGGER = LogManager.getLogger(DirectoryManager.class);
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private Set<Directory> directories = new HashSet<>();
	
	public DirectoryManager() {
	}
	
	public boolean registerNewDirectory(Directory dir) {
		try {
			lock.writeLock().lock();
			
			if(this.directories.add(dir)) {
				if(Files.createDirectories(dir.getPath()) != null) LOGGER.info("Directory "+dir.getName()+" has been successfully created!");
				else LOGGER.info("Directory "+dir.getName()+" has been successfully loaded!");
				
				return true;
			}
			return false;
		}catch(SecurityException | IOException e) {
			LOGGER.error("An error occured while creating directory",dir.getName(),". Skipping 1 directory.",e.getMessage());
			return false;
		}finally {
			lock.writeLock().unlock();
		}
	}
	
	public boolean registerNewDirectoryIfAbsent(Directory dir) {
			if(!this.directories.contains(dir)) {
				try {
					lock.writeLock().lock();
					if(this.directories.add(dir)) {
						if(Files.createDirectories(dir.getPath()) != null) LOGGER.info("Directory "+dir.getName()+" has been successfully created!");
						else LOGGER.info("Directory "+dir.getName()+" has been successfully loaded!");
						
						return true;
					}
					return false;
				}catch(SecurityException | IOException e) {
					LOGGER.error("An error occured while creating directory",dir.getName(),". Skipping 1 directory.",e.getMessage());
					return false;
				}finally {
					lock.writeLock().unlock();
				}
			}
			return false;
		
	}
	
	public void registerNewDirectory(Directory... directories) {
		try {
			lock.writeLock().lock();
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
		}finally {
			lock.writeLock().unlock();
		}
		
	}
	
	public boolean removeDirectory(Directory dir) {
		try {
			lock.writeLock().lock();
			if(this.directories.remove(dir)) {
				LOGGER.info("Directory "+dir.getName()+" has been successfully unloaded!");
				return true;
			}
			
			return false;
		}finally {
			lock.writeLock().unlock();
		}
	}

	public Directory getLoadedDirectory(String nameId) {
		try {
			lock.readLock().lock();
			return this.directories.parallelStream().filter(dir -> dir.isNameSimilar(nameId)).findFirst().orElse(null);
		}finally {
			lock.readLock().unlock();
		}
	}
	
	public Directory getLoadedDirectory(Path path) {
		try {
			lock.readLock().lock();
			return this.directories.parallelStream().filter(dir -> dir.isPathSimilar(path)).findFirst().orElse(null);
		}finally {
			lock.readLock().unlock();
		}
	}
	
	public boolean exist(Directory dir) {
		try {
			lock.readLock().lock();
			return this.directories.contains(dir);
		}finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Get the total of stored directories
	 * @return the size of the directories's list
	 */
	public int getDirectoriesNumber() {
		try {
			lock.readLock().lock();
			return this.directories.size();
		}finally {
			lock.readLock().unlock();
		}
	}
}