package com.tinkerpop.gremlin.server;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class RequestMessageTest {
    @Test
    public void shouldReturnEmptyOptionalSessionId() {
        final RequestMessage msg = new RequestMessage.Builder("op").noSession().build();
        assertFalse(msg.optionalSessionId().isPresent());
    }

    @Test
    public void shouldReturnSessionIdAsOptional() {
        final RequestMessage msg = new RequestMessage.Builder("op").newSession().build();
        assertTrue(msg.optionalSessionId().isPresent());
        assertEquals(msg.sessionId, msg.optionalSessionId().get());
    }

    @Test
    public void shouldReturnEmptyOptionalArg() {
        final RequestMessage msg = new RequestMessage.Builder("op").build();
        assertFalse(msg.optionalArgs("test").isPresent());
    }

    @Test
    public void shouldReturnArgAsOptional() {
        final RequestMessage msg = new RequestMessage.Builder("op").build();
        msg.args.put("test", "testing");
        assertEquals("testing", msg.optionalArgs("test").get());
    }
}
