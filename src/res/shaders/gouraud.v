#version 330
layout(location = 0) in vec4 ecken;
layout(location = 1) in vec2 texture;

out vec2 texToFrag;

uniform mat4 m, v, p;

void main(void) {
	texToFrag=texture;
	gl_Position=m*v*p*ecken;
}