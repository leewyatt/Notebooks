package com.itcodebox.notebooks.projectservice;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.itcodebox.notebooks.entity.NoteNavigationItem;

/**
 * @Author LittleRed
 * @Date 2021/10/2 15:34
 * @Description TODO
 * @Since version-1.0
 */
public class NoteChooseByNameFilterCondiguration extends ChooseByNameFilterConfiguration<NoteNavigationItem.NameAndValue> {
    @Override
    protected String nameForElement(NoteNavigationItem.NameAndValue nameAndValue) {
        return nameAndValue.getValue();
    }
}
