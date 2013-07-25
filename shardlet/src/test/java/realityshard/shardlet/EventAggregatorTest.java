/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.junit.Test;


/**
 *
 * @author _rusty
 */
public class EventAggregatorTest 
{
    
    public class TestEvent implements Event {}
    
    
    private static final int TIMEOUT = 10; // ms
    
    private boolean gotCalled = false;
    
    
    @Test
    public void Test()
    {
        // init the objs
        GlobalExecutor.init(new ScheduledThreadPoolExecutor(12));
        EventAggregator ea = new EventAggregator();
        
        // register
        ea.register(this);
        
        // call it by giving an 'interfaced' obj
        ea.triggerEvent(getEvent());
        
        // wait till true or timeout
        long t = System.currentTimeMillis();
        while (!gotCalled) { if (diffNow(t) > TIMEOUT) { break; } }
        
        // handler should have been called
        assert gotCalled;
    }
    
    
    @EventHandler
    public void onTestEvent(TestEvent evt)
    {
        gotCalled = true;
    }
    
    
    private Event getEvent()
    {
        return (Event) new TestEvent();
    }
    
    
    private long diffNow(long then) { return System.currentTimeMillis() - then; }
}
