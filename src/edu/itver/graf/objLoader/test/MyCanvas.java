/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
http://www.sweethome3d.com/freeModels.jsp
http://www.turbosquid.com/Search/3D-Models/free/obj
*/

package edu.itver.graf.objLoader.test;

import com.jogamp.opengl.*;
import static com.jogamp.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import com.jogamp.opengl.awt.GLCanvas;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import edu.itver.graf.objLoader.ObjReader;
import edu.itver.graf.objLoader.Object3d;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 *
 * @author gmendez
 */
public class MyCanvas extends GLCanvas implements GLEventListener, KeyListener {

    private static final int CANVAS_WIDTH = 640;  // width of the drawable
    private static final int CANVAS_HEIGHT = 480; // height of the drawable
    public static final int FPS = 10; // animator's target frames per second

    int width = CANVAS_WIDTH;
    int height = CANVAS_HEIGHT;
    float fovy = 45.0f;
    GLU glu;  // for the GL Utility
    GLUT glut;
    Object3d obj1, obj2;
    
    float rotate = 0.0f;
    float scale = 1.0f;
    
    String fileName = "";
    
    //  Posición de la fuente de luz 1
    float plx = 0.0f;
    float ply = 0.0f;
    float plz = 0.0f;

    //  Posición de la cámara
    float pcx = 0.0f;
    float pcy = 2.0f;
    float pcz = 10.0f;    

    //  Dirección de la camara
    float pvx = 0.0f;
    float pvy = 0.0f;
    float pvz = 0.0f;   
        
    //  Posición de los objetos
    float pox = 0.0f;
    float poy = 0.0f;
    float poz = 0.0f;    
    
    int objSel = 0;
    private Object3d obj3;
    
    
    public MyCanvas(String fileName) {
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        this.addGLEventListener(this);
        this.addKeyListener(this);
        this.fileName = fileName;
    }

    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();

        glu = new GLU();
        glut = new GLUT();

        // ----- Your OpenGL initialization code here -----
        gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f); // set background (clear) color
        gl.glClearDepth(1.0f);      // set clear depth value to farthest               

        gl.glEnable(GL2.GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL2.GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST); // best perspective correction
        gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting        

        this.setLight(gl);

        gl.glEnable(GL2.GL_NORMALIZE);

        ObjReader or = new ObjReader();        

        // Cargar los objetos dentro del contexto de OpenGL
        try {
            obj1 = or.Load("./data/tree1.obj");
            System.out.println("Objeto 1 cargado :" + this.fileName);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();  // get the OpenGL 2 graphics context

        setLight(gl);

        setCamera(gl);

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glTranslatef(pox, poy, poz);        
        gl.glRotatef(rotate, 0.0f, 1.0f, 0.0f);
        gl.glScalef(scale, scale, scale);
        
        /*
        // ----- Your OpenGL rendering code here (Render a white triangle for testing) -----
        float material[] = {0.0f, 1.0f, 0.0f, 1.0f};

        //gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT);
        //gl.glColor3f(0.8f, 1.0f, 0.4f);
        
        gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_AMBIENT_AND_DIFFUSE, material,0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 100.0f);
        
        gl.glEnable(GL2.GL_COLOR_MATERIAL);        
        
        */
        
        if (obj1 != null) {
            obj1.draw(gl);
        }
                    
        if (obj2 != null) {
            obj2.draw(gl);
        }
                
        if (obj3 != null) {
            obj3.draw(gl);
        }
        
        
        gl.glFlush();

        //rotate += 1.5;
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
        GL2 gl = glad.getGL().getGL2();  // obtiene un contexto grafico OpenGL 2

        width = this.getWidth();
        height = this.getHeight();

        if (height == 0) {
            height = 1;   // prevent divide by zero
        }

        float aspect = (float) width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix

        glu.gluPerspective(fovy, aspect, 0.1, 10.0); // fovy, aspect, zNear, zFar
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity(); // reset

    }

    private void setCamera(GL2 gl) {
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(pcx, pcy, pcz, pvx, pvy, pvz, 0, 1, 0);

        // Change back to model view matrix.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void drawSpiral(GL2 gl) {
        float r = 0.5f;
        float x, y, z;
        gl.glBegin(GL.GL_POINTS);
        {
            for (float a = 0; a <= 1440; a += 5) {
                r -= 0.5f / 300;
                float ang = (float) Math.toRadians(a);
                gl.glVertex3f(
                        (float) (r * Math.cos(ang)),
                        (float) a / 1440f - 0.5f,
                        (float) (r * Math.sin(ang))
                );
            }
        }
        gl.glEnd();
    }

    private void setLight(GL2 gl) {
        float SHINE_ALL_DIRECTIONS = 1;
        float lightPos[] = {plx, ply, plz, SHINE_ALL_DIRECTIONS};
        float lightAmbient[] = {0.5f, 0.5f, 0.5f, 1.0f};
        float lightDiffuse[] = {0.8f, 0.8f, 0.8f, 1.0f};
        float lightSpecular[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float lmodel_ambient[] = {0.0f, 0.0f, 0.0f, 1.0f};
        float local_view[] = {0.0f};

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // Set light parameters.
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightSpecular, 0);

        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view, 0);

        // Enable lighting in GL.
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHTING);
        
        /*
        gl.glTranslatef(plx, ply, plz);
        glut.glutSolidSphere(1.0f, 50, 50);
                
        gl.glLoadIdentity();
        */

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        int cod = e.getExtendedKeyCode();
     
        if(cod>=KeyEvent.VK_0 && cod<=KeyEvent.VK_9){
            objSel=cod-KeyEvent.VK_0;
            System.out.println("Objeto seleccionado: # "+objSel);
        }
                    
        
        if (e.isShiftDown()) {  // SHIFT y Flechas controlan la LUZ
            switch (cod) {
                case KeyEvent.VK_R:
                    //  Posición de la fuente de luz 1
                    plx = 0.0f;
                    ply = 0.0f;
                    plz = 0.0f;
                    break;
                
                case KeyEvent.VK_LEFT :  // Izquierda
                    plx -= 1;
                    break;

                case KeyEvent.VK_UP:  // Arriba
                    ply += 1;
                    break;

                case KeyEvent.VK_RIGHT:  // Derecha
                    plx += 1;
                    break;

                case KeyEvent.VK_DOWN:  // Abajo
                    ply -= 1;
                    break;

                case KeyEvent.VK_A:  // Zoom In
                    plz -= 1;
                    break;
                case KeyEvent.VK_S:  // Zoom out
                    plz += 1;
                    break;

            }
            System.out.println("Posicion de la luz, plx=" + plx + ", ply=" + ply + ", plz=" + plz);            
            
        } else if(e.isAltDown())  {  // ALT y Flechas controlan  posicion de la cámara
            
            switch (cod) {
                
                case KeyEvent.VK_R :  // Izquierda
                                    //  Posición de la cámara
                    pcx = 0.0f;
                    pcy = 2.0f;
                    pcz = 10.0f;    
                    break;
                    
                case KeyEvent.VK_LEFT :  // Izquierda
                    pcx -= 1;
                    break;

                case KeyEvent.VK_UP:  // Arriba
                    pcy += 1;
                    break;

                case KeyEvent.VK_RIGHT:  // Derecha
                    pcx += 1;
                    break;

                case KeyEvent.VK_DOWN:  // Abajo
                    pcy -= 1;
                    break;

                case KeyEvent.VK_A:  // Zoom In 
                    pcz -= 1;
                    break;
                case KeyEvent.VK_S:  // Zoom out
                    pcz += 1;
                    break;

            }            
        
            System.out.println("Posicion de la cámara, pcx=" + pcx + ", pcy=" + pcy + ", pcz=" + pcz);
            
        } else if(e.isControlDown())  {  // CTRL y Flechas controlan la dirección de vista de la cámara
            
            switch (cod) {
                
                case KeyEvent.VK_R :  // Izquierda
                                    //  Posición de la cámara
                    pvx = 0.0f;
                    pvy = 0.0f;
                    pvz = 0.0f;    
                    break;
                    
                case KeyEvent.VK_LEFT :  // Izquierda
                    pvx -= 1;
                    break;

                case KeyEvent.VK_UP:  // Arriba
                    pvy += 1;
                    break;

                case KeyEvent.VK_RIGHT:  // Derecha
                    pvx += 1;
                    break;

                case KeyEvent.VK_DOWN:  // Abajo
                    pvy -= 1;
                    break;

                case KeyEvent.VK_A:  // Zoom In
                    pvz -= 1;
                    break;
                case KeyEvent.VK_S:  // Zoom out
                    pvz += 1;
                    break;
            }            
        
            System.out.println("Posicion de la vista de la cámara, pvx=" + pvx + ", pvy=" + pvy + ", pvz=" + pvz);


        } else {
            
            switch (cod) {
                case KeyEvent.VK_R :  // Izquierda
                                    //  Posición de la cámara
                    pox = 0.0f;
                    poy = 0.0f;
                    poz = 0.0f;    
                    break;
            
                case KeyEvent.VK_LEFT :  // Izquierda
                    pox -= 1;
                    break;

                case KeyEvent.VK_UP:  // Arriba
                    poy += 1;
                    break;

                case KeyEvent.VK_RIGHT:  // Derecha
                    pox += 1;
                    break;

                case KeyEvent.VK_DOWN:  // Abajo
                    poy -= 1;
                    break;

                case KeyEvent.VK_A:  // Zoom In
                    poz -= 1;
                    break;
                case KeyEvent.VK_S:  // Zoom out
                    poz += 1;
                    break;
                case KeyEvent.VK_X:  // 
                    rotate += 1;
                    break;
                case KeyEvent.VK_Z:  // Zoom out
                    rotate -= 1;
                    break;                    
            }         
            
            
            System.out.println("Posicion del objeto: "+rotate+", pox=" + pox + ", poy=" + poy + ", poz=" + poz);
        }

        

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
