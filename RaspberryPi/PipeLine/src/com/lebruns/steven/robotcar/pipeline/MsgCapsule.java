/**
 * @file MsgCapsule.java
 *
 *
 */
/**
 * @copyright Statement goes here.
 */
package com.lebruns.steven.robotcar.pipeline;

/**
 * @author Steven F. LeBrun
 *
 * A MsgCapsule is an abstract base class that contain messages being sent
 * one thread to another using Pipelines.
 */
public abstract class MsgCapsule
{
	/**
	 * Static value shared by all MsgCapsule instances.  This variable is 
	 * used to keep track of the next message ID that can be assigned.
	 */
	static private int nextId = 0;
	
	/**
	 * This constant is used to prevent message ID values from going negative
	 * or overflowing.
	 */
	static private final int MAX_ID = 0x7FFFFFFF;
	
	/**
	 * The Message Capsule ID.  This should be a unique Identifier for each
	 * instance of a MsgCapsule.  Due to the fact that message IDs can be 
	 * recycled means that there is a small chance that an ID can be reused.
	 * It is assumed that by the time an ID is reused, the previous message
	 * capsule using that ID should no longer exist.
	 */
	private int id;
	
	/**
	 * Flag that indicates whether the message capsule has been processed or
	 * not.  @b True means that the message capsule has been processed.
	 * @b False means that the message capsule is waiting to be processed.
	 */
	private boolean processed;
	
	/**
	 * Constructor that initializes the base Message Capsule.
	 */
	public MsgCapsule()
	{
		id = getNextId();
		processed = false;
	}
	
	/**
	 * Getter Method that provides access to the Message Capsule ID.
	 * 
	 * @return Returns the Message Capsule ID.
	 */
	public int getMsgId()
	{
		return id;
	}
	
	/**
	 * Getter Method that provides access to whether or not the message
	 * capsule has been processed.
	 * 
	 * @return Returns @b true if the message capsule has been processed.
	 *         Returns @b false if the message capsule is waiting to be 
	 *         processed.
	 */
	public boolean beenProcessed()
	{
		return processed;
	}
	
	/**
	 * Changes the state of the message capsuled from waiting to be processed
	 * to has been processed.
	 */
	public void hasBeenProcessed()
	{
		processed = true;
	}
	
	/**
	 * Gets the next ID for a Message capsule.
	 * 
	 * If the next ID reaches the maximum value, then the next ID returned
	 * will be 1, starting the cycle over again.  The assumption is that ID 
	 * will still be unique when the cycle starts over since message capsules
	 * should have a finite lifetime and never last long enough to use all the
	 * possible IDs.
	 * 
	 * @return  Returns the next ID for a message capsule.
	 */
	private synchronized int getNextId()
	{
		nextId = ( nextId < MAX_ID ) ? ++nextId : 1 ;
		
		return nextId;
	}

}   // end of class MsgCapsule
