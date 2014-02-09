#version 330
in vec4 ecken;
in vec2 texture;
out vec2 texToFrag;
uniform mat4 frustMatrix;

void main(void) {
	texToFrag=texture;
	gl_Position=frustMatrix*ecken;
}