/**
 * 
 */
package com.lebruns.steven.robotcar;

/**
 * @author Steven F. LeBrun
 *
 * The Controller Class is a the abstract base class for classes used
 * to control something in the Robot Car.  Each controller is an independent
 * thread that operates to perform a task that usually revolves around a
 * piece of hardware.
 */
public abstract class Controller extends Thread
{
	private PipeSink pipeType;

	/**
	 * 
	 */
	public Controller( PipeSink type )
	{
		pipeType = type;
	}

	/**
	 * @param arg0
	 */
	public Controller(PipeSink type, Runnable arg0)
	{
		super(arg0);
		
		pipeType = type;
	}

	/**
	 * @param arg0
	 */
	public Controller(PipeSink type, String arg0)
	{
		super(arg0);
		
		pipeType = type;
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public Controller(PipeSink type, ThreadGroup arg0, Runnable arg1)
	{
		super(arg0, arg1);
		
		pipeType = type;
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public Controller(PipeSink type, ThreadGroup arg0, String arg1)
	{
		super(arg0, arg1);
		
		pipeType = type;
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public Controller(PipeSink type, Runnable arg0, String arg1)
	{
		super(arg0, arg1);
		
		pipeType = type;
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public Controller(PipeSink type, ThreadGroup arg0, Runnable arg1, String arg2)
	{
		super(arg0, arg1, arg2);
		
		pipeType = type;
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public Controller(PipeSink type, ThreadGroup arg0, Runnable arg1, String arg2, long arg3)
	{
		super(arg0, arg1, arg2, arg3);
		
		pipeType = type;
	}
	
	public PipeSink getType()
	{
		return pipeType;
	}
	
	/**
	 * Each Controller Class must provide their own run() method.
	 */
	public abstract void run();

}   // end of class Controller
