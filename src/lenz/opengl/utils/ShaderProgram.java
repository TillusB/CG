package lenz.opengl.utils;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.InputStream;
import java.util.Scanner;

public class ShaderProgram {
	private int id;

	public ShaderProgram(String resourceNameWithoutExtension) {
		this(resourceNameWithoutExtension + ".v", resourceNameWithoutExtension + ".f");
	}

	public ShaderProgram(String vertexResourceName, String fragmentResourceName) {
		this(vertexResourceName, null, fragmentResourceName);
	}

	public ShaderProgram(String vertexResourceName, String geometryResourceName, String fragmentResourceName) {
		int vertexShader = compileFromSource(vertexResourceName, GL_VERTEX_SHADER);
		int fragmentShader = compileFromSource(fragmentResourceName, GL_FRAGMENT_SHADER);

		id = glCreateProgram();
		glAttachShader(id, vertexShader);
		glAttachShader(id, fragmentShader);

		if (geometryResourceName != null) {
			int geometryShader = compileFromSource(geometryResourceName, GL_GEOMETRY_SHADER);
			glAttachShader(id, geometryShader);
		}

		glLinkProgram(id);
		if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException(glGetProgramInfoLog(id, glGetProgrami(id, GL_INFO_LOG_LENGTH)));
		}
	}

	public int getId() {
		return id;
	}

	private InputStream createInputStreamFromResourceName(String resourceName) {
		return getClass().getResourceAsStream("/res/shaders/" + resourceName);
	}

	private int compileFromSource(String resourceName, int type) {
		try (Scanner in = new Scanner(createInputStreamFromResourceName(resourceName))) {
			String source = in.useDelimiter("\\A").next();
			int shader = glCreateShader(type);
			glShaderSource(shader, source);
			glCompileShader(shader);

			String compileLog = glGetShaderInfoLog(shader, glGetShaderi(shader, GL_INFO_LOG_LENGTH));
			if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
				throw new RuntimeException("Shader " + resourceName + " not compiled: " + compileLog);
			}
			if (!compileLog.isEmpty()) {
				System.err.println(resourceName + ": " + compileLog);
			}

			return shader;
		}
	}
}
