/*
 * Copyright (c) 2000-2004 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package com.intellij.psi;

import com.intellij.psi.jsp.JspTokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.DTDTokenType;
import com.intellij.psi.xml.XmlTokenType;

public interface TokenType extends
                           JavaTokenType,
                           JavaDocTokenType,
                           JspTokenType,
                           XmlTokenType,
                           DTDTokenType,
                           com.intellij.aspects.psi.gen.TokenType {
  IElementType NEW_LINE_INDENT = new IElementType("NEW_LINE_INDENT", null);
}
