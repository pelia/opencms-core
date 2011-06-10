/*
 * File   : $Source: /alkacon/cvs/opencms/src-gwt/org/opencms/gwt/client/validation/I_CmsValidationHandler.java,v $
 * Date   : $Date: 2011/06/10 06:57:36 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2011 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.gwt.client.validation;

import org.opencms.gwt.shared.CmsValidationResult;

/**
 * This interface is used to receive notifications about the status of the validation.<p>
 * 
 * @author Georg Westenberger
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 8.0.0
 */
public interface I_CmsValidationHandler {

    /**
     * This method is called when the validation has been completed.<p>
     * 
     * @param ok true if all fields have been successfully validated 
     */
    void onValidationFinished(boolean ok);

    /**
     * This method is called when a single field has been validated.<p>
     * 
     * @param fieldId the field which has been validated 
     * @param result the result of the validation 
     */
    void onValidationResult(String fieldId, CmsValidationResult result);

}