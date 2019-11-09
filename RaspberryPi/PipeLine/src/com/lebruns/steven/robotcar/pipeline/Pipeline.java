/**
 * @file Pipeline.java
 *
 *
 */
/**
 * @copyright Statement goes here.
 */
package com.lebruns.steven.robotcar.pipeline;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @author Steven F. LeBrun
 * 
 * A Pipeline is a FIFO Queue used to send messages between threads.
 * A Pipeline has two queues, each carrying messages in the same direction.
 * The first queue carries regular messages.  The second queue carries
 * Out of Band (OOB) messages.  
 * 
 * OOB messages have a higher priority for  * processing and effectively are 
 * used to bypass the FIFO queue.  OOB messages are used for such requests as
 * Start, Stop, Pause, Resume, Terminate.
 * 
 * The Pipeline class is an abstract base class.  Each derived class will 
 * support a specific subtype of message and provide virtual methods to 
 * determine if message subtypes are allowed.
 * 
 * Pipelines are designed to have multiple senders and a single consumer.
 * 
 * If bi-directional communications is required, create two Pipelines, each
 * one sending messages in a different direction.
 *
 */
public abstract class Pipeline
{
	private ConcurrentLinkedQueue<MsgCapsule> fifoQueue    = null;
	private ConcurrentLinkedQueue<MsgCapsule> oobQueue     = null;
	
	
	private String lock = "Pipeline Lock";


	/**
	 * 
	 */
	public Pipeline()
	{
		fifoQueue = new ConcurrentLinkedQueue<MsgCapsule>();
		oobQueue  = new ConcurrentLinkedQueue<MsgCapsule>();
	}
	
	/**
	 * Determines if the MsgCapsule is a child class that is
	 * supported by this Pipeline child class.
	 * 
	 * @param msg The MsgCapsule in question.
	 * 
	 * @return Returns @b true if the MsgCapsule is of a derived type
	 *         that is supported by this Pipeline.  Returns @b false
	 *         if the MsgCapsule derived type is not supported.
	 */
	public abstract boolean is_supported( MsgCapsule msg );
	
	//========================================================================
	// Client Side [sender] public methods.
	//========================================================================
	
	
	/**
	 * Add a MsgCapsule to the queue.  The type of MsgCapsule determines if
	 * the message will be added and whether it is placed in the regular queue
	 * or the out of band queue.
	 * 
	 * Note, if any thread is waiting for a command, this method will
	 * send a notification and wake them [all] up.
	 * 
	 * @param msg The Message to be sent. 
	 * 
	 * @exception Throws a PipelineIllegalParameterException if the message is
	 *            not a MsgCapsule type supported by this Pipeline.
	 */
	public void put(MsgCapsule msg)
		throws PipelineIllegalParameterException
	{
		// Determine if message is supported by this Pipeline
		if ( ! this.is_supported(msg) )
		{
			String error;
			
			error = "MsgCapsule Type: " + 
			        msg.getClass().getName() +
			        " is not supported by "  +
			        this.getClass().getName();
			
			throw new PipelineIllegalParameterException( error );
		}
		
		if ( msg instanceof OutOfBandMessage )
		{
			oobQueue.add(msg);
		}
		else
		{
			fifoQueue.add(msg);
		}
		
		synchronized (lock)
		{
			lock.notifyAll();
		}
		
		return;
		
	}   // end of put()
	

	
	//========================================================================
	// Consumer Side [receiver] public methods
	//========================================================================

	/**
	 * Retrieves the next message that was sent.  This message is normally
	 * obtained from the fifo Queue unless there is a message in the
	 * Out Of Band Queue.  Out Of Band messages are processed before 
	 * the messages in the fifo Queue since an Out of Band message
	 * may result in draining the fifo Queue without processing the
	 * fifo messages.
	 * 
	 * This message does not block.
	 * 
	 * @return Returns the next message object to be processed
	 *         If there are no commands, a NULL is returned.
	 */
	public MsgCapsule  getNextCommand()
	{
		MsgCapsule next = oobQueue.poll();
		
		if ( next == null )
		{
			next = fifoQueue.poll();
		}
		
		return next;
		
	}   // end of getNextCommand()
	
	/**
	 * Retrieves the next message to be processed.
	 *  
	 * If no commands are available, this method will 
	 * wait until one is available.
	 * 
	 * @return Returns the next Command to be processed.
	 */
	public MsgCapsule waitForNextCommand()
	{
		MsgCapsule next = null;
		
		while ( next == null )
		{
			next = getNextCommand();
			
			if ( next == null )
			{
				try
				{
					synchronized (lock)
					{
						lock.wait();
					}
				}
				catch ( Exception exc )
				{
					System.out.println("Waiting for next Command Message");
					System.out.println(exc.getMessage());
					exc.printStackTrace(System.out);
				}
			}
		}
		
		return next;
		
	}   // end of waitForNextCommand()
	
	
	/**
	 * Flushes all messages in the fifo Queue without processing them.
	 */
	public void flush()
	{
		fifoQueue.clear();
		
		synchronized (lock)
		{
			lock.notifyAll();
		}
	}
	
	public boolean isEmpty()
	{
		if ( fifoQueue.isEmpty() && oobQueue.isEmpty() )
		{
			return true;
		}
		
		return false;
	}


}   // end of class Pipeline
