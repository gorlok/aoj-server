/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia «gorlok» 
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.argentumonline.server.forum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

/**
 * @author gorlok
 */
public class Forum {

    final static int MAX_FORUM_MESSAGES = 35;

    /**
     * Unique ID of this forum
     */
    private String forumId;
    
    /**
     * New post are added at front.
     * When forum is full, oldest post is discarded.
     */
    private List<ForumMessage> posts = new ArrayList<>();

    /** Creates a new instance of Forum */
    public Forum(String forumId) {
        this.forumId = forumId;
    }

    public String getForumId() {
        return this.forumId;
    }

    public void addPost(String title, String text, String author) {
        if (this.posts.size() >= MAX_FORUM_MESSAGES) {
            this.posts.remove(this.posts.size()-1);
        }
        this.posts.add(0, new ForumMessage(title, text, author, new Date()));
    }
    
    public List<ForumMessage> getPosts() {
		return posts;
	}
    
    public String toJson() {
    	Gson gson = new Gson();
    	return gson.toJson(this);
    }
    
    public static Forum fromJson(String jsonText) {
    	Gson gson = new Gson();
    	return gson.fromJson(jsonText, Forum.class);
    }

}
