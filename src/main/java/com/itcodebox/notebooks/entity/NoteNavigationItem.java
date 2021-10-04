package com.itcodebox.notebooks.entity;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

/**
 * @Author LittleRed
 * @Date 2021/10/2 14:56
 * @Description TODO
 * @Since version-1.0
 */
public class NoteNavigationItem implements NavigationItem {

    private PsiElement psielment;
    private NavigationItem navigationItem;
    private String name;
    private NameAndValue nameAndValue;
    
    public static class NameAndValue{
        private String name;
        private String value;

        public NameAndValue(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    } 

    public NoteNavigationItem(String name, PsiElement psiElement) {
        this.psielment = psiElement;
        if (psiElement instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiElement;
            this.name = method.getName();
        }
        if (psiElement instanceof NavigationItem) {
            this.navigationItem = (NavigationItem) psiElement;
        }
        this.name = name;
        nameAndValue = new NameAndValue(name, name);
    }

    @Nullable
    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return new NoteItemPresentation(name);
    }

    @Override
    public void navigate(boolean b) {
        if (null != navigationItem) {
            navigationItem.navigate(b);
        }
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    public String getValue() {
        return this.name;
    }

    public NameAndValue getNameAndValue() {
        return nameAndValue;
    }
}
