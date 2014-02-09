import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import lenz.opengl.AbstractSimpleBase;
import lenz.opengl.utils.ShaderProgram;
import lenz.opengl.utils.Texture;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL20.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Example extends AbstractSimpleBase {
	Matrix4f projection = new Matrix4f();
	Matrix4f mvp;		
	ShaderProgram spGouraud;
	Pyramid p;
	ShaderProgram spShader;
	int vaoId, vboIdE, vboIdC;
	//
	
	
	float[] ecken;
	float[] texture;
	float[] tetra;

	public static void main(String[] args) {
		new Example().start();
	}

	@Override
	protected void initOpenGL() {
		p = new Pyramid();
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
		Texture texture = new Texture("rosa_camouflage.jpg");
		
		projection.m00 = 2*nearPlane/(right-left);
		projection.m20 = A;
		
		projection.m11 = 2*nearPlane/(top-bottom);
		projection.m21 = B;
		
		projection.m22 = C;
		projection.m32 = D;
		
		projection.m23 = -1;
				
		wuerfel(4);
		spGouraud = new ShaderProgram("gouraud");
		glBindAttribLocation(spGouraud.getId(), 0, "ecken");
		glLinkProgram(spGouraud.getId());
		glUseProgram(spGouraud.getId());
		glEnable(GL_CULL_FACE);
		//glEnable(GL_DEPTH_TEST);
		glShadeModel(GL_SMOOTH);
		
		/*tetraeder();
		spShader = new ShaderProgram("shader");
		glBindAttribLocation(spShader.getId(), 0, "ecken");
		glLinkProgram(spShader.getId());
		glUseProgram(spShader.getId());
						
		glEnable(GL_CULL_FACE);
		//glEnable(GL_DEPTH_TEST);
		glShadeModel(GL_SMOOTH);*/
	}

	/*
	 * Draws a cube
	 */
	protected void wuerfel(int size){
		//GEGEN DEN URZEIGERSINN <- AUßENSEITE
		ecken = new float[]{
			//front
			size,size,size,
			-size,size,size,
			-size,-size,size,
			size,-size,size,
			
			//right
			size,size,-size,
			size,size,size,
			size,-size,size,
			size,-size,-size,
			
			//back
			-size,-size,-size,
			-size,size,-size,
			size,size,-size,
			size,-size,-size,
			
			//left
			-size,size,size,
			-size,size,-size,
			-size,-size,-size,
			-size,-size,size,
			
			//top
			-size,size,-size,
			-size,size,size,
			size,size,size,
			size,size,-size,
			
			//bottom
			-size,-size,size,
			-size,-size,-size,
			size,-size,-size,
			size,-size,size
		};
		
		
		
		//Create Model		
		FloatBuffer edgeBuffer = BufferUtils.createFloatBuffer(ecken.length);
		edgeBuffer.put(ecken);
		edgeBuffer.flip();
		
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		vboIdE = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboIdE);
		glBufferData(GL_ARRAY_BUFFER, edgeBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		texture = new float[] {
				//front
				1,1,
				0,1,
				0,0,
				1,0,
				//right
				1,0,
				1,1,
				0,1,
				0,0,
				//back
				0,0,
				0,1,
				1,1,
				1,0,
				//left
				1,1,
				1,0,
				0,0,
				0,1,
				//top
				0,0,
				0,1,
				1,1,
				1,0,
				//bottom
				0,1,
				0,0,
				1,0,
				1,1
		};
		vboIdC = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboIdC);
		glVertexAttribPointer(1,3,GL_FLOAT,false,0,0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		//Create Texture
		FloatBuffer texBuffer = BufferUtils.createFloatBuffer(texture.length);
		texBuffer.put(texture);
		texBuffer.flip();
		
		vboIdC = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboIdC);
		glBufferData(GL_ARRAY_BUFFER, texBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1,2,GL_FLOAT,false,0,0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);	}
	
	long lastTime = Sys.getTime();
	float w = 0;
	float scale = 1;
	float rotate = 0;
	float height = 0;
	@Override
	protected void render() {
		p.render();
		mvp = new Matrix4f(projection);
		mvp.translate(new Vector3f(0,-5,-50f));
		long t = Sys.getTime();
		glClear(GL_COLOR_BUFFER_BIT);
		//translate projection matrix
		mvp.rotate(rotate,new Vector3f(0,1,0));
		mvp.translate(new Vector3f(20,height,0));
		mvp.rotate(w, new Vector3f(1,0,0));
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
			scale+=0.1f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
			scale-=0.1f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
			rotate+=0.1f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
			rotate-=0.1f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			height += 0.5f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_C)){
			height -= 0.5f;
		}
		mvp.scale(new Vector3f(scale,scale,scale));
		long diff = lastTime - t;
		w += 0.3 * diff/1000f;
		lastTime = t;
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
		glUniformMatrix4(glGetUniformLocation(spGouraud.getId(), "frustMatrix"), false, fbM);
		//glUniformMatrix4(glGetUniformLocation(spShader.getId(), "frustMatrix"), false, fbM);
	}
}