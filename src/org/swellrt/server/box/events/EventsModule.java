package org.swellrt.server.box.events;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import org.swellrt.server.box.events.gcm.GCMDeviceStore;
import org.swellrt.server.box.events.gcm.GCMDeviceStoreFake;
import org.swellrt.server.box.events.gcm.GCMDispatcher;
import org.swellrt.server.box.events.gcm.GCMSubscriptionManager;
import org.swellrt.server.box.events.gcm.GCMSubscriptionManagerImpl;
import org.swellrt.server.box.events.gcm.GCMSubscriptionStore;
import org.swellrt.server.box.events.gcm.GCMSubscriptionStoreFake;

/**
 * Events module provides generation of data model events (map entry updated,
 * list item added...) and an extensible dispatching infrastructure.
 *
 * For now, an GCM notifications module is provided
 *
 * @author pablojan@gmail.com (Pablo Ojanguren)
 *
 */
public class EventsModule extends AbstractModule {

  @Override
  protected void configure() {

    bind(EventQueue.class).to(EventQueueImpl.class).in(Singleton.class);
    bind(DeltaBasedEventSource.class).in(Singleton.class);
    bind(EventDispatcher.class).to(EventDispatcherImpl.class).in(Singleton.class);
    bind(GCMDispatcher.class).in(Singleton.class);
    bind(GCMSubscriptionStore.class).to(GCMSubscriptionStoreFake.class).in(Singleton.class);
    bind(GCMDeviceStore.class).to(GCMDeviceStoreFake.class).in(Singleton.class);
    bind(GCMSubscriptionManager.class).to(GCMSubscriptionManagerImpl.class).in(Singleton.class);
  }

}
