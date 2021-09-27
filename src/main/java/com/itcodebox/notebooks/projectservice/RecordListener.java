package com.itcodebox.notebooks.projectservice;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;

import java.util.List;

/**
 * @author LeeWyatt
 */
public interface RecordListener {

    Topic<RecordListener> TOPIC = Topic.create("RecordChanged", RecordListener.class);


    /**
     * Called when reload Data
     */
    void onRefresh();

    /*-----Notebook  operate-------*/

    /**
     * Called when a new notebook is added.
     */
    void onNotebookAdd(Project eventProject, Notebook notebook,boolean isSelected,boolean editing);

    /**
     * Called when a notebook is updated.
     */
    void onNotebookUpdated(Project eventProject, Notebook[] notebooks);

    /**
     * Called when a notebook's title is updated.
     */
    void onNotebookTitleUpdated(Project eventProject,Notebook notebook);

    /**
     * Called when a notebook is removed.
     */
    void onNotebookRemoved(Project eventProject, Notebook notebook);

    /**
     * Called when a notebook is dragged to chang the showOrder
     */
    void onNotebookDragMove(Project eventProject, Notebook notebook, int rowFromIndex, int rowEndIndex);

    /*-----------Chapter operate--------------------*/

    /**
     * Called when a new chapter is added.
     */
    void onChapterAdd(Project eventProject, Chapter chapter,boolean isSelected,boolean editing);

    /**
     * Called when a chapter is updated.
     */
    void onChapterUpdated(Project eventProject, Chapter[] chapters);

    /**
     * Called when a chapter's title is updated.
     */
    void onChapterTitleUpdated(Project eventProject, Chapter chapter);

    /**
     * Called when a chapter is removed.
     */
    void onChapterRemoved(Project eventProject, Chapter chapter);

    /**
     * Called when a notebook is dragged to chang the showOrder
     */
    void onChapterDragMove(Project eventProject, Chapter chapter, int rowFromIndex, int rowEndIndex);


    /*-----------Note operate-----------------------*/

    /**
     * Called when a new note is added.
     */
    void onNoteAdd(Project eventProject, Note note);

    /**
     * Called when batch addition
     * 都有共同的NotebookId 和 ChapterId
     */
    void onNoteAdd(Project eventProject, List<Note> noteList);

    /**
     * Called when a note is updated.
     */
    void onNoteUpdated(Project eventProject, Note[] notes);

    /**
     * Called when a note's title is updated.
     */
    void onNoteTitleUpdated(Project eventProject, Note note);

    /**
     * Called when a note is removed.
     */
    void onNoteRemoved(Project eventProject, Note note);

    /**
     * Called when a note is dragged to change the showOrder
     */
    void onNoteDragMove(Project eventProject,Note note, int rowFromIndex, int rowEndIndex);

}
