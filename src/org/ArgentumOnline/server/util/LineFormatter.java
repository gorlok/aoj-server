/**
 * LineFormatter.java
 *
 * Created on 17 de septiembre de 2003, 22:44
 * 
    AOJava Server
    Copyright (C) 2003-2007 Pablo Fernando Lillia (alias Gorlok)
    Web site: http://www.aojava.com.ar
    
    This file is part of AOJava.

    AOJava is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    AOJava is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
    
 */
package org.ArgentumOnline.server.util;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.text.SimpleDateFormat;

/**
 * Simple formatter. Output one line of text.
 * @author Pablo
 *
 */
public class LineFormatter extends Formatter {
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ssZ");
	Date date = new Date();

	/* Simple formatter. Output one line of text.
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public synchronized String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer(1000);
        buf.append(rec.getLevel());
        buf.append(';');
        this.date.setTime(rec.getMillis());
        buf.append(this.sdf.format(this.date));
        buf.append(';');
        buf.append(rec.getSourceClassName());
        buf.append(';');
        buf.append(rec.getSourceMethodName());
        buf.append(';');
        buf.append(formatMessage(rec));
        buf.append('\n');
        return buf.toString();
	}
}
