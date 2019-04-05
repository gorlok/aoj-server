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
package org.argentumonline.server.guilds;

/**
 *
 * @author gorlok
 */
public class GuildRequest {
    
    String userName;
    String desc;
    
    /** Creates a new instance of GuildRequest */
    public GuildRequest(String userName, String desc) {
        this.userName = userName;
        this.desc = desc;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public String getDesc() {
        return this.desc;
    }
    
    public boolean isFromUserName(String userName) {
    	return this.userName != null &&
    			this.userName.equalsIgnoreCase(userName);
    }
    
}
