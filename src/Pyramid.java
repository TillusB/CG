import lenz.opengl.AbstractSimpleBase;
import lenz.opengl.utils.ShaderProgram;
import lenz.opengl.utils.Texture;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Pyramid extends AbstractSimpleBase {
	Matrix4f projection1 = new Matrix4f();
	public Matrix4f mvp;		
	int vaoId, vboIdE, vboIdT;
	private Texture texture;
	private ShaderProgram spShader;
	
	
	
	public float [] ecken;
	float [] base;
	
	public Pyramid(){
		float nearPlane = 1;
		float farPlane = 100;
		float right = 1;
		float left = -1;
		float bottom = -1;
		float top = 1;
		
		float A = (right + left) / (right-left);
		float B = (top+bottom) / (top-bottom);
		float C = -((farPlane+nearPlane) / (farPlane - nearPlane));
		float D = -((2*farPlane*nearPlane)/(farPlane - nearPlane));
		float height = 1.63f;
		float size = 1f;
		ecken = new float[]{
				//front
				0,height*5,0,
				-size*5,0,size*5,
				size*5,0,size*5,
				//right
				0,height*5,0,
				size*5,0,size*5,
				size*5,0,-size*5,
				
				//back
				0,height*5,0,
				size*5,0,-size*5,
				-size*5,0,-size*5,
				
				//left
				0,height*5,0,
				-size*5,0,-size*5,
				-size*5,0,size*5,
				
				//bottom-left
				size*5,0,size*5,
				-size*5,0,size*5,
				-size*5,0,-size*5,
				
				//bottom-right
				size*5,0,size*5,
				-size*5,0,-size*5,
				size*5,0,-size*5
		};
		spShader = new ShaderProgram("pyramide");

		mvp = new Matrix4f();

		FloatBuffer edgeBuffer = BufferUtils.createFloatBuffer(ecken.length);
        edgeBuffer.put(ecken);
        edgeBuffer.flip();
        
        float[] texUv = new float[] {
        		1, 0,
				0, 1,
				1, 1,
				
				1, 0,
				1, 1,
				0, 0,
				
				1, 0,
				0, 0,
				0, 1,
				
				0, 0,
				1, 1,
				0, 1,
				
				0,1,
				0,0,
				1,0,
				
				0,1,
				1,0,
				1,1
				
				
        };

        //Create UV
        FloatBuffer uvBuffer = BufferUtils.createFloatBuffer(texUv.length);
        uvBuffer.put(texUv);
        uvBuffer.flip();
        glUseProgram(spShader.getId());
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboIdE = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboIdE);
        glBufferData(GL_ARRAY_BUFFER, edgeBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        vboIdT = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboIdT);
        glBufferData(GL_ARRAY_BUFFER, uvBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

		texture = new Texture("rosa_camouflage.jpg");
		glBindAttribLocation(spShader.getId(), 0, "ecken");
        glBindAttribLocation(spShader.getId(), 1, "texUv");
        glUseProgram(0);
	}
	@Override
	protected void render() {
		glUseProgram(spShader.getId());
		glClear(GL_COLOR_BUFFER_BIT);
		//Draw Object
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawArrays(GL_QUADS,0,ecken.length/3);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		FloatBuffer fbM = BufferUtils.createFloatBuffer(16);
		mvp.store(fbM);
		fbM.flip();
		glUseProgram(spShader.getId());
		glUniformMatrix4(glGetUniformLocation(spShader.getId(), "frustMatrix"), false, fbM);
		//glUniformMatrix4(glGetUniformLocation(spShader.getId(), "frustMatrix"), false, fbM);
		glUseProgram(0);
	}

	@Override
	protected void initOpenGL() {

		float nearPlane = 1;
		float farPlane = 100;
		float right = 1;
		float left = -1;
		float bottom = -1;
		float top = 1;
		
		float A = (right + left) / (right-left);
		float B = (top+bottom) / (top-bottom);
		float C = -((farPlane+nearPlane) / (farPlane - nearPlane));
		float D = -((2*farPlane*nearPlane)/(farPlane - nearPlane));
		
		
		projection1.m00 = 2*nearPlane/(right-left);
		projection1.m20 = A;
		
		projection1.m11 = 2*nearPlane/(top-bottom);
		projection1.m21 = B;
		
		projection1.m22 = C;
		projection1.m32 = D;
		
		projection1.m23 = -1;
		glUseProgram(spShader.getId());
		glBindAttribLocation(spShader.getId(), 0, "ecken");
		glBindAttribLocation(spShader.getId(), 1, "texUv");
		glLinkProgram(spShader.getId());
		glUseProgram(spShader.getId());
		glEnable(GL_CULL_FACE);
		//glEnable(GL_DEPTH_TEST);
		glShadeModel(GL_SMOOTH);
		glUseProgram(0);
	}
	
}
