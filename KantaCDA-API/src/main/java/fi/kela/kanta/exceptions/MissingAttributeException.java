/*******************************************************************************
 * Copyright 2017 Kansaneläkelaitos
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package fi.kela.kanta.exceptions;

import java.text.MessageFormat;

public class MissingAttributeException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final String backupPattern = "Sisäinen attribuutti ''{0}'' puuttuu.";
    private final String basicMsg;

    public MissingAttributeException(String attributeName) {
        basicMsg = buildMessage(MissingAttributeException.backupPattern, attributeName);
    }

    @Override
    public String getMessage() {

        return basicMsg;
    }

    @Override
    public String getLocalizedMessage() {

        return basicMsg;
    }

    private String buildMessage(String pattern, String attributeName) {
        MessageFormat mf = new MessageFormat(pattern);
        Object[] obj = { attributeName };
        return mf.format(obj);
    }
}
