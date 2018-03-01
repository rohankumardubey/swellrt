package org.swellrt.beta.model.wave.mutable;

import org.swellrt.beta.common.SException;
import org.swellrt.beta.model.SList;
import org.swellrt.beta.model.SMap;
import org.swellrt.beta.model.SMutationHandler;
import org.swellrt.beta.model.SNode;
import org.swellrt.beta.model.SObject;
import org.swellrt.beta.model.SObservableNode;
import org.swellrt.beta.model.SPrimitive;
import org.swellrt.beta.model.SStatusEvent;
import org.swellrt.beta.model.SText;
import org.swellrt.beta.model.SVisitor;
import org.swellrt.beta.model.js.Proxy;
import org.swellrt.beta.model.js.SMapProxyHandler;
import org.swellrt.beta.model.presence.SPresenceEvent;
import org.swellrt.beta.model.presence.SSession;
import org.waveprotocol.wave.model.wave.InvalidParticipantAddress;

import jsinterop.annotations.JsIgnore;


/**
 *
 * Main class providing object-based data model built on top of Wave data model.
 * <p>
 * A {@link SObject} represents a JSON-like data structure containing a nested
 * structure of {@link SMap}, {@link SList} and {@link SPrimitive} values.
 * <p>
 * The underlying implementation based on the Wave data model provides:
 * <ul>
 * <li>Real-time sync of SObject state from changes of different users, even
 * between different servers (see Wave Federation)</li>
 * <li>In-flight persistence of changes with eventual consistency (see Wave
 * Operational Transformations)</li>
 * </ul>
 * <p>
 *
 * @author pablojan@gmail.com (Pablo Ojanguren)
 *
 */
public class SWaveObject implements SObject, SObservableNode {


  private SWaveMap root;

  private SObject.StatusHandler statusHandler = null;

  private final SWavePresence presence;
  private final SWaveMetadata metadata;
  private final SWaveNodeManager waveManager;


  /**
   * Private constructor.
   *
   * @param idGenerator
   * @param domain
   * @param wave
   */
  protected SWaveObject(SWaveNodeManager waveManager) {
    this.waveManager = waveManager;
    root = waveManager.getDataRoot();
    this.presence = new SWavePresence(waveManager.getTransient().getPresenceStatusMap(),
        waveManager.getSession());
    this.presence.start();
    this.metadata = waveManager.getMetadata();
  }

  /**
   * Unit test only.
   * TODO fix visibility
   */
  public void clearCache() {
    root.clearCache();
  }

  @Override
  public void addListener(SMutationHandler h, String path) throws SException {
    root.addListener(h, path);
  }


  @Override
  public void removeListener(SMutationHandler h, String path) throws SException {
    root.removeListener(h, path);
  }

  @Override
  public void listen(SMutationHandler h) throws SException {
    this.root.listen(h);
  }

  @Override
  public void unlisten(SMutationHandler h) throws SException {
    this.root.unlisten(h);
  }

  @Override
  public String getId() {
    return waveManager.getId();
  }

  @Override
  public void addParticipant(String participantId) throws InvalidParticipantAddress {
    waveManager.addParticipant(participantId);
  }

  @Override
  public void removeParticipant(String participantId) throws InvalidParticipantAddress {
    waveManager.removeParticipant(participantId);
  }

  @Override
  public SSession[] getParticipants() {
    return waveManager.getMetadata().getParticipants();
  }


  @Override
  public void setPublic(boolean isPublic) {
    waveManager.setPublic(isPublic);
  }

  public boolean isPublic() {
    return waveManager.isPublic();
  }

  public Object js() {
    return new Proxy(root, new SMapProxyHandler());
  }

  @Override
  public void setStatusHandler(StatusHandler h) {
    this.statusHandler = h;
  }

  @JsIgnore
  public void onStatusEvent(SStatusEvent e) {
    if (statusHandler != null)
      statusHandler.exec(e);
  }

  @Override
  public void setPresenceHandler(SPresenceEvent.Handler handler) {
    presence.registerHandler(handler);
  }

  //
  // SMap interface
  //


  @Override
  public SNode pick(String key) throws SException {
    return root.pick(key);
  }

  @Override
  public SMap put(String key, SNode value) throws SException {
    return root.put(key, value);
  }

  @Override
  public SMap put(String key, Object object) throws SException {
    return root.put(key, object);
  }

  @Override
  public void remove(String key) throws SException {
    root.remove(key);
  }

  @Override
  public void removeSafe(String key) throws SException {
    root.removeSafe(key);
  }

  @Override
  public boolean has(String key) throws SException {
    return root.has(key);
  }

  @Override
  public String[] keys() throws SException {
    return root.keys();
  }

  @Override
  public void clear() throws SException {
    root.clear();
  }

  @Override
  public boolean isEmpty() {
    return root.isEmpty();
  }

  @Override
  public int size() {
    return root.size();
  }


  public boolean isNew() {
    return false;
  }

  @SuppressWarnings("rawtypes")
  @JsIgnore
  @Override
  public void accept(SVisitor visitor) {
    visitor.visit(this);
  }

  //
  // -----------------------------------------------------
  //

  @Override
  public void set(String path, Object value) {
    SNode.set(this, path, value);
  }

  @Override
  public void push(String path, Object value) {
    SNode.push(this, path, value);
  }

  @Override
  public Object pop(String path) {
    return SNode.pop(this, path);
  }

  @Override
  public int length(String path) {
    return SNode.length(this, path);
  }

  @Override
  public boolean contains(String path, String property) {
    return SNode.contains(this, path, property);
  }

  @Override
  public void delete(String path) {
    SNode.delete(this, path);
  }

  @Override
  public Object get(String path) {
    return SNode.get(this, path);
  }

  @Override
  public SNode node(String path) throws SException {
    return SNode.node(this, path);
  }

  @Override
  public SMap asMap() {
    return this.root;
  }

  @Override
  public SList<? extends SNode> asList() {
    throw new IllegalStateException("Node is not a list");
  }

  @Override
  public String asString() {
    throw new IllegalStateException("Node is not a string");
  }

  @Override
  public double asDouble() {
    throw new IllegalStateException("Node is not a number");
  }

  @Override
  public int asInt() {
    throw new IllegalStateException("Node is not a number");
  }

  @Override
  public boolean asBoolean() {
    throw new IllegalStateException("Node is not a boolean");
  }

  @Override
  public SText asText() {
    throw new IllegalStateException("Node is not a text");
  }

  @Override
  public SMap getUserStore() {
    return waveManager.getUserRoot();
  }

  @Override
  public SMap getTransientStore() {
    return waveManager.getTransientRoot();
  }

  @Override
  public String[] _getWavelets() {
    return waveManager.getWavelets();
  }

  @Override
  public String[] _getDocuments(String waveletId) {
    return waveManager.getDocuments(waveletId);
  }

  @Override
  public String _getContent(String waveletId, String documentId) {
    return waveManager.getContent(waveletId, documentId);
  }

  @Override
  public SNode[] values() throws SException {
    return root.values();
  }

}
