import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import lenz.opengl.AbstractSimpleBase;
import lenz.opengl.utils.ShaderProgram;
import lenz.opengl.utils.Texture;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;


import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL20.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Example extends AbstractSimpleBase {
	Matrix4f projection;
	Matrix4f view;
	
	ShaderProgram spGouraud;
	Pyramid p;
	ShaderProgram spShader;
	
	Matrix4f modelM;
	
	int vaoId, vboIdE, vboIdC;
	
	final float fov = 60f;
	final float farPlane = 100;
	final float nearPlane = 1;
	//
	
	
	float[] ecken;
	float[] texture;

	public static void main(String[] args) {
		new Example().start();
	}
	
	private void projection(){
		DisplayMode dm = Display.getDisplayMode();
		float size = (float)dm.getWidth() / dm.getHeight();
		float yScale = 1 / (float)Math.tan(Math.toRadians( (double)fov / 2));
		float xScale = yScale / size;
		
		float frustumLength = farPlane - nearPlane;
		
		projection = new Matrix4f();
		
		projection.m00 = xScale;
		projection.m11 = yScale;
		projection.m22 = -((farPlane + nearPlane) / frustumLength);
		projection.m23 = -1;
		projection.m32 = -((2 * nearPlane * farPlane) / frustumLength);
		projection.m33 = 0;
	}
	
	private void initView(){
		view = new Matrix4f();
		view.rotate((float)Math.toRadians(20), new Vector3f(1,0,0));
		view.translate(new Vector3f(0,-15,-25));
	}

	@Override
	protected void initOpenGL() {
		glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        projection();
        initView();
        modelM = new Matrix4f();
		p = new Pyramid();
		wuerfel(4);

		Texture texture = new Texture("rosa_camouflage.jpg");		
				
		spGouraud = new ShaderProgram("gouraud");
		glBindAttribLocation(spGouraud.getId(), 0, "ecken");
		glLinkProgram(spGouraud.getId());
		glUseProgram(spGouraud.getId());
		glBindAttribLocation(spGouraud.getId(), 0, "ecken");
		glBindAttribLocation(spGouraud.getId(), 1, "texUv");
		glEnable(GL_CULL_FACE);
		//glEnable(GL_DEPTH_TEST);
		glShadeModel(GL_SMOOTH);
		glUseProgram(0);
		

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
		
		FloatBuffer texBuffer = BufferUtils.createFloatBuffer(texture.length);
		vboIdC = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboIdC);
        glBufferData(GL_ARRAY_BUFFER, texBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
	}
	
	long lastTime = Sys.getTime();
	float w = 0;
	float scale = 1;
	float rotate = 0;
	float height = 0;
	float radius = 20;
	@Override
	protected void render() {
		FloatBuffer modelBuf = BufferUtils.createFloatBuffer(16);
        modelM.store(modelBuf);
        modelBuf.flip();
        
		/*glUseProgram(spShader.getId());
		p.render();
		p.mvp = new Matrix4f(projection);
		glUseProgram(0);*/
		modelM.translate(new Vector3f(0,-5,-50f));
		long t = Sys.getTime();
		glClear(GL_COLOR_BUFFER_BIT);
		//translate projection matrix
		modelM.rotate(rotate,new Vector3f(0,1,0));
		modelM.translate(new Vector3f(radius,height,0));
		modelM.rotate(w, new Vector3f(1,0,0));
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
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			radius+=0.5f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			radius-=0.5f;
		}
		modelM.scale(new Vector3f(scale,scale,scale));
		long diff = lastTime - t;
		w += 0.3 * diff/1000f;
		lastTime = t;
		//Draw Object
		glUseProgram(spGouraud.getId());
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawArrays(GL_QUADS,0,ecken.length/3);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		glUseProgram(0);
		
		FloatBuffer projectionBuf = BufferUtils.createFloatBuffer(16);
        projection.store(projectionBuf);
        projectionBuf.flip();
        
        FloatBuffer viewBuf = BufferUtils.createFloatBuffer(16);
        view.store(viewBuf);
        viewBuf.flip();

		glUseProgram(spGouraud.getId());
		 glUniformMatrix4(glGetUniformLocation(spGouraud.getId(), "m"), false, modelBuf);
		 glUniformMatrix4(glGetUniformLocation(spGouraud.getId(), "v"), false, viewBuf);
	     glUniformMatrix4(glGetUniformLocation(spGouraud.getId(), "p"), false, projectionBuf);		
	     //glUniformMatrix4(glGetUniformLocation(spShader.getId(), "frustMatrix"), false, fbM);
		glUseProgram(0);
	}
}