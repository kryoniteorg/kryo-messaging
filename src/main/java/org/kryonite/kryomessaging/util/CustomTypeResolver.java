package org.kryonite.kryomessaging.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

public class CustomTypeResolver extends ObjectMapper.DefaultTypeResolverBuilder {

  public CustomTypeResolver() {
    super(ObjectMapper.DefaultTyping.NON_FINAL, LaissezFaireSubTypeValidator.instance);
  }

  @Override
  public boolean useForType(JavaType t) {
    return !t.isContainerType() && (t.isInterface() || t.isAbstract() || t.hasGenericTypes());
  }
}
