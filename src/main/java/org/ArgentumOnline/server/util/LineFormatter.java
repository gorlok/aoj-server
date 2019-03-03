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
