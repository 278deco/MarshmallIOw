package marshmalliow.core.file.dotenv;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A container for the environment variables.<br/>
 * This class allows to retrieve the environment variables and cast them to the desired type.
 * <p>
 * The environment variables are stored in a mapping where the key is the name of the variable
 * and the value is the content of the variable.
 * 
 * @see Map
 * @version 1.0.0
 * @since 0.3.0
 * @author 278deco
 */
public class DotenvContainer {

	private final Map<String, String> envMapping;

	/**
	 * Create a new {@link DotenvContainer} with an empty mapping.
	 */
	public DotenvContainer() {
		this.envMapping = new HashMap<>();
	}

	/**
	 * Create a new {@link DotenvContainer} with the provided mapping.
	 * @param envMapping The mapping of the environment variables
	 */
	public DotenvContainer(Map<String, String> envMapping) {
		this.envMapping = envMapping;
	}

	/**
	 * Add an environment variable to the current mapping.<br/>
	 * If the key already exists, the value is replaced.
	 * 
	 * @param container The {@link DotenvContainer} containing the environment variable to add
	 */
	public void addAll(DotenvContainer container) {
		envMapping.putAll(container.envMapping);
	}
	
	/**
	 * Add all the environment variables in the provided mapping to the current mapping.<br/>
	 * If a key already exists, the value is replaced.
	 * 
	 * @param env The mapping of the environment variables to add
	 */
	public void addAll(Map<String, String> env) {
		envMapping.putAll(env);
	}

	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * If the key is not found, return null
	 * 
	 * @param key The key of the environment variable
	 * @return The value of the environment variable
	 */
	public String getEnv(String key) {
		return envMapping.get(key);
	}

	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * If the key is not found, return the <code>defaultValue</code>
	 * 
	 * @param key The key of the environment variable
	 * @param defaultValue The default value if the key is not found
	 * @return The value of the environment variable
	 */
	public String getEnvOrDefault(String key, String defaultValue) {
		return envMapping.getOrDefault(key, defaultValue);
	}
	
	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * If the key is not found, return the value of the <code>otherKey</code>
	 * 
	 * @param key      The key of the environment variable
	 * @param otherKey The key to get the value from if the key is not found
	 * @return The value of the environment variable
	 */
	public String getEnvOrElse(String key, String otherKey) {
		return envMapping.getOrDefault(key, envMapping.get(otherKey));
	}

	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * The key is then casted to an integer. If the key doesn't exist or cannot be 
	 * parsed, returns {@link Optional#empty()}.
	 * 
	 * @param key The key of the environment variable
	 * @return An {@link Optional} containing the value of the environment variable 
	 * 		   as an integer
	 * @see Integer#parseInt(String)
	 */
	public Optional<Integer> getEnvAsInt(String key) {
		try {
			return Optional.of(Integer.parseInt(envMapping.get(key)));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * The key is then casted to a double. If the key doesn't exist or cannot be
	 * parsed, returns {@link Optional#empty()}.
	 * 
	 * @param key The key of the environment variable
	 * @return An {@link Optional} containing the value of the environment variable
	 *         as a double
	 * @see Double#parseDouble(String)
	 */
	public Optional<Double> getEnvAsDouble(String key) {
		try {
			return Optional.of(Double.parseDouble(envMapping.get(key)));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * The key is then casted to a boolean. If the key doesn't exist or cannot be
	 * parsed, returns {@link Optional#empty()}.<br/>
	 * <p>
	 * As described by {@link Boolean#parseBoolean(String)}, the value is considered true 
	 * if it is not case-sensitive "true". Otherwise, it is false.
	 * 
	 * @param key The key of the environment variable
	 * @return An {@link Optional} containing the value of the environment variable
	 *         as a boolean
	 * @see Boolean#parseBoolean(String)
	 */
	public Optional<Boolean> getEnvAsBoolean(String key) {
		final String value = envMapping.get(key);
		if (value == null)
			return Optional.empty();
		return Optional.of(Boolean.parseBoolean(value));
	}

	/**
	 * Get the mapping of the environment variables.
	 * 
	 * @return The mapping of the environment variables
	 */
	public Map<String, String> getEnvMapping() {
		return Collections.unmodifiableMap(envMapping);
	}

	@Override
	public boolean equals(Object obj) {
		return envMapping.equals(obj);
	}

	@Override
	public String toString() {
		return envMapping.toString();
	}

}
