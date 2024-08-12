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
	
	/**
	 * Register a new directory to the directories's list and create it if it doesn't exist.
	 * @param dir the directory to be registered
	 * @return true if the directory has been successfully registered, false otherwise
	 */
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
	
	/**
	 * Register a new directory to the directories's list and create it if it doesn't exist.<br/>
	 * This method adds the directory only if it doesn't exist in the directories's list.
	 * @param dir The directory to be registered
	 * @return true if the directory has been successfully registered, false otherwise
	 */
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
	
	/**
	 * Register every directory to the directories's list and create them if they don't exist.
	 * 
	 * @param directories The directories to be registered
	 */
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
	
	/**
	 * Register every directory to the directories's list and create them if they
	 * don't exist.
	 * 
	 * @param directories The directories to be registered
	 * @return true if the directories have been successfully registered, false
	 *         otherwise
	 */
	public boolean registerNewDirectories(Set<Directory> directories) {
		try {
			lock.writeLock().lock();
			try {
				this.directories.addAll(directories);
			}catch(UnsupportedOperationException | ClassCastException | IllegalArgumentException e) {
				return false;
			}
			
			return true;
		}finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Remove a directory from the directories's list.
	 * @param dir The directory to be removed
	 * @return true if the directory has been successfully removed, false otherwise
	 */
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

	/**
	 * Get a directory from the directories's list using its name.
	 * @param nameId The name of the directory to be retrieved
	 * @return The directory if it exists in the directories's list, null otherwise
	 */
	public Directory getLoadedDirectory(String nameId) {
		try {
			lock.readLock().lock();
			return this.directories.parallelStream().filter(dir -> dir.isNameSimilar(nameId)).findFirst().orElse(null);
		}finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Get a directory from the directories's list using its path.
	 * 
	 * @param path The path of the directory to be retrieved
	 * @return The directory if it exists in the directories's list, null otherwise
	 */
	public Directory getLoadedDirectory(Path path) {
		try {
			lock.readLock().lock();
			return this.directories.parallelStream().filter(dir -> dir.isPathSimilar(path)).findFirst().orElse(null);
		}finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Check if a directory exists in the directories's list.
	 * @param dir The directory to be checked
	 * @return true if the directory exists in the directories's list, false otherwise
	 */
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