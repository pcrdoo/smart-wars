package util;

import java.lang.Math;

public class Vector2D {

  protected double dX;
  protected double dY;

  // Constructor methods ....

  public Vector2D() {
     dX = dY = 0.0;
  }

  public Vector2D( double dX, double dY ) {
     this.dX = dX;
     this.dY = dY;
  }

  // Convert vector to a string ...
   
  public String toString() {
     return "Vector2D(" + dX + ", " + dY + ")";
  }

  // Compute magnitude of vector ....

  public double length() {
     return Math.sqrt ( dX*dX + dY*dY );
  }

  // Sum of two vectors ....

  public Vector2D add( Vector2D v1 ) {
      Vector2D v2 = new Vector2D( this.dX + v1.dX, this.dY + v1.dY );
      return v2;
  }

  // Subtract vector v1 from v .....

  public Vector2D sub( Vector2D v1 ) {
      Vector2D v2 = new Vector2D( this.dX - v1.dX, this.dY - v1.dY );
      return v2;
  }

  // Scale vector by a constant ...

  public Vector2D scale( double scaleFactor ) {
      Vector2D v2 = new Vector2D( this.dX*scaleFactor, this.dY*scaleFactor );
      return v2;
  }

  // Normalize a vectors length....

  public Vector2D normalize() {
     Vector2D v2 = new Vector2D();

     double length = Math.sqrt( this.dX*this.dX + this.dY*this.dY );
     if (length != 0) {
       v2.dX = this.dX/length;
       v2.dY = this.dY/length;
     }

     return v2;
  }   

  // Dot product of two vectors .....

  public double dotProduct ( Vector2D v1 ) {
       return this.dX*v1.dX + this.dY*v1.dY;
  }

  // Exercise methods in Vector2D class

  public static void main ( String args[] ) {
     Vector2D vA = new Vector2D( 1.0, 2.0);
     Vector2D vB = new Vector2D( 2.0, 2.0);
     
     System.out.println( "Vector vA =" + vA.toString() );
     System.out.println( "Vector vB =" + vB.toString() );

     System.out.println( "Vector vA-vB =" + vA.sub(vB).toString() );
     System.out.println( "Vector vB-vA =" + vB.sub(vA).toString() );

     System.out.println( "vA.normalize() =" + vA.normalize().toString() );
     System.out.println( "vB.normalize() =" + vB.normalize().toString() );

     System.out.println( "Dot product vA.vB =" + vA.dotProduct(vB) );
     System.out.println( "Dot product vB.vA =" + vB.dotProduct(vA) );
  }
}