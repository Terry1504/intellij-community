package com.jetbrains.python.validation;

import com.intellij.lang.annotation.Annotation;
import com.intellij.openapi.util.TextRange;
import com.jetbrains.python.PythonDocStringFinder;
import com.jetbrains.python.documentation.*;
import com.jetbrains.python.highlighting.PyHighlighter;
import com.jetbrains.python.psi.*;

/**
 * Highlights doc strings in classes, functions, and files.
 */
public class DocStringAnnotator extends PyAnnotator {

  @Override
  public void visitPyFile(final PyFile node) {
    annotateDocStringStmt(PythonDocStringFinder.find(node));
  }

  @Override
  public void visitPyFunction(final PyFunction node) {
    annotateDocStringStmt(PythonDocStringFinder.find(node.getStatementList()));
  }

  @Override
  public void visitPyClass(final PyClass node) {
    annotateDocStringStmt(PythonDocStringFinder.find(node.getStatementList()));
  }

  @Override
  public void visitPyAssignmentStatement(PyAssignmentStatement node) {
    PyExpression left = node.getLeftHandSideExpression();
    if (left != null && "__doc__".equals(left.getName())) {
      PyExpression right = node.getAssignedValue();
      if (right instanceof PyStringLiteralExpression) {
        Annotation ann = getHolder().createInfoAnnotation(right, null);
        ann.setTextAttributes(PyHighlighter.PY_DOC_COMMENT);
        annotateDocStringStmt((PyStringLiteralExpression)right);
      }
    }
  }

  @Override
  public void visitPyExpressionStatement(PyExpressionStatement node) {
    if (node.getExpression() instanceof PyStringLiteralExpression &&
        EpydocUtil.isVariableDocString((PyStringLiteralExpression)node.getExpression())) {
      annotateDocStringStmt((PyStringLiteralExpression)node.getExpression());
    }
  }

  private void annotateDocStringStmt(final PyStringLiteralExpression stmt) {
    if (stmt != null) {
      final PyDocumentationSettings settings = PyDocumentationSettings.getInstance(stmt.getProject());
      if (settings.isPlain(stmt.getContainingFile()))
        return;       // nothing to annotate if docstrings are plain
      else {
        String[] tags = settings.isEpydocFormat(stmt.getContainingFile()) ? EpydocString.ALL_TAGS : SphinxDocString.ALL_TAGS;
        int pos = 0;
        while(true) {
          TextRange textRange = DocStringReferenceProvider.findNextTag(stmt.getText(), pos, tags);
          if (textRange == null) break;
          Annotation annotation = getHolder().createInfoAnnotation(textRange.shiftRight(stmt.getTextRange().getStartOffset()), null);
          annotation.setTextAttributes(PyHighlighter.PY_DOC_COMMENT_TAG);
          pos = textRange.getEndOffset();
        }
      }
    }
  }
}
