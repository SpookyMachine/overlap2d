/*
 * ******************************************************************************
 *  * Copyright 2015 See AUTHORS file.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package com.uwsoft.editor.mvc.controller.sandbox;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.Overlap2D;
import com.uwsoft.editor.gdx.mediators.SceneControlMediator;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.legacy.data.CompositeItemVO;
import com.uwsoft.editor.utils.runtime.ComponentRetriever;

import java.util.HashMap;

/**
 * Created by azakhary on 4/28/2015.
 */
public class AddToLibraryCommand extends RevertableCommand {

    private String createdLibraryItemName;
    private CompositeItemVO overwritten;

    @Override
    public void doAction() {
        Object[] payload = getNotification().getBody();

        Entity item = ((Entity) payload[0]);
        createdLibraryItemName = (String) payload[1];

        SceneControlMediator sceneControl = sandbox.getSceneControl();
        HashMap<String, CompositeItemVO> libraryItems = sceneControl.getCurrentSceneVO().libraryItems;

        if(libraryItems.containsKey(createdLibraryItemName)) {
            overwritten = libraryItems.get(createdLibraryItemName);
        }

        CompositeItemVO newVO = new CompositeItemVO();
        newVO.loadFromEntity(item);
        libraryItems.put(createdLibraryItemName, newVO);

        //mark this entity as belonging to library
        MainItemComponent mainItemComponent = ComponentRetriever.get(item, MainItemComponent.class);
        mainItemComponent.libraryLink = createdLibraryItemName;

        facade.sendNotification(Overlap2D.LIBRARY_LIST_UPDATED);
    }

    @Override
    public void undoAction() {
        SceneControlMediator sceneControl = sandbox.getSceneControl();
        HashMap<String, CompositeItemVO> libraryItems = sceneControl.getCurrentSceneVO().libraryItems;

        libraryItems.remove(createdLibraryItemName);

        if(overwritten != null) {
            libraryItems.put(createdLibraryItemName, overwritten);
        }

        facade.sendNotification(Overlap2D.LIBRARY_LIST_UPDATED);
    }
}
