#version 330
in vec2 texToFrag;
out vec4 farbe;

uniform sampler2D textureSampler;
 
 void main(void){
 	if(mod(texToFrag.x, 5) == 0){
 		farbe = texture(textureSampler, texToFrag);
 	}
 	else{
  		farbe = texture(textureSampler, texToFrag);	
  	}
 }