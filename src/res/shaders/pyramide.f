#version 330
in vec2 texToFrag;
out vec4 farbe;

uniform sampler2D textureSampler;
 
 void main(void){
  	farbe = texture(textureSampler, texToFrag);	
 }