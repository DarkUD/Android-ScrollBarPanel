package com.dafruits.darkud;

import java.io.Serializable;

//Class			: 	Vect 
//Description 	:	Permet de saisir les données relatives à un vecteur et de 
//					faire des opérations basiques 

public class Vect implements Serializable{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8985715854677884531L;
	
	//coordinates
	public double x = 0;
	public double y = 0;
	
	//constructors
	public Vect(){
		//Default constructor
		x = 0;
		y = 0;
	}
	
	public Vect(double x,double y)
	{
		//Default constructor 
		this.x = x;
		this.y = y;
	}
	
	//Operations on vector
	public double length()
	{
		//length of the vector 
		return Math.sqrt(x * x + y * y);
	}
	
	public Vect perp()
	{
		//return the vector perpendicular to this one
		//if v(x,y) then v.perpX() -> v(y,-x)
		return new Vect(-y,x);
	}	
	
	public Vect normalize()
	{
		//return the normalized coordinates of this vector
		//normalizing a vector means he got a length of 1
		//to normalize a vector we divide his coordinates by his length
		return new Vect(x/length(),y/length());
	}
	
	public Vect add(Vect v)
	{
		//add this vector with another and return result
		return new Vect(x + v.x,y+v.y);
	}
	
	public Vect sub(Vect v)
	{
		//Subtract this vector with another and return result
		//if O(x,y) & A(a,b) O.sub(A) = OA (a-x,b-y)
		return new Vect(v.x-x,v.y-y);
	}
	
	public Vect mul(int real)
	{
		//multiply this vector by a real and return result
		return new Vect(real * x,real * y);
	}
	
	public Vect mul(double d)
	{
		//multiply this vector by a double and return result
		return new Vect(d * x,d * y);
	}
	
	public double dot(Vect v)
	{
		//dot product of two vectors
		//used generally to calculate angle between two vectors
		return x*v.x + y *v.y;
	}
	
	public double cross(Vect v)
	{
		return x*v.y - y*v.x;
	}
	
	public double angle(Vect v)
	{
		//use of the dot product to have the resulting angle
		Vect A = normalize();
		Vect B = v.normalize();
		
		double dot = 0;
		double add = 0;		
		
		while((dot = A.dot(B)) < 0)
		{
			B = B.perp();
			add += Math.PI/2;
		}
		
		double angle = Math.acos(A.dot(B)) + add; 
		
		if(add == 0)
		{
			B = B.perp();
			dot = A.dot(B);
			
			if(dot < 0)angle = -angle;			
		}
		
		return angle;
	}
	
	public double overlap(Vect v)
	{
		//return how much the two vectors are overlaping
		//0 or negative means they not
		if((x > v.y || v.x > y))
		{
			return 0;
		}
								
		return (Math.abs((x - y)/2) + Math.abs((v.x - v.y)/2) - Math.abs((x + y - v.x - v.y)/2));
	}
	
	public Vect translate(Vect v)
	{
		//calculate resulting vector after applying a translation vector
		//in truth it s just a add operation
		//this method is there for clarification when used in code
		return add(v);		
	}
		
	public Vect rotate(Vect pivot,double angle)
	{
		//calculate the resulting vector after applying a rotation 
		//pivot represent the center of the rotation
		Vect result = new Vect(x,y);

		//we translate back to origin
		result = translate(pivot.mul(-1));
		
		//we apply rotation
		result = result.rotate(angle);
		
		//we translate b to original position
		result = result.translate(pivot);
		
		return result;
	}
	
	public Vect rotate(double angle)
	{
		//Basic rotation
		return new Vect(x * Math.cos(angle) - y * Math.sin(angle)
						,x * Math.sin(angle) + y*Math.cos(angle));
	}
	
	public Vect scale(Vect pivot,double ratio)
	{
		//calculate the resulting vector after applying a scale
		//pivot represent the invariant point on the scale
		Vect result = new Vect(x,y);
				
		//as for rotation, we translate back to origin
		result = translate(pivot.mul(-1));
		
		//we apply scale 
		result = result.mul(ratio);
		
		//we translate back to original position
		result = result.translate(pivot);
		
		return result;		
	}
	
	public Vect shear(Vect pivot,double xRatio,double yRatio)
	{
		//calculate the resulting vector after applying a shear operation
		//pivot represent the invariant point on the scale
		Vect result = new Vect(x,y);
				
		//as for rotation, we translate back to origin
		result = translate(pivot.mul(-1));
		
		//we apply the matrix 
		result.x = result.x * xRatio;
		result.y = result.y * yRatio;
		
		//we translate back to original position
		result = result.translate(pivot);
		
		return result;
	}
	
	@Override
	public String toString() {
		
		return "("+(float)x+","+(float)y+")";
	}
	
}
