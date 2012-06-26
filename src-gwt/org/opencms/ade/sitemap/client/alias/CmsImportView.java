/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) Alkacon Software (http://www.alkacon.com)
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

package org.opencms.ade.sitemap.client.alias;

import org.opencms.ade.sitemap.client.CmsSitemapView;
import org.opencms.ade.sitemap.client.alias.CmsImportResultList.I_Css;
import org.opencms.ade.sitemap.shared.I_CmsAliasConstants;
import org.opencms.ade.upload.client.ui.CmsUploadButton;
import org.opencms.ade.upload.client.ui.I_CmsUploadButtonHandler;
import org.opencms.gwt.client.CmsCoreProvider;
import org.opencms.gwt.client.rpc.CmsLog;
import org.opencms.gwt.client.ui.CmsPopup;
import org.opencms.gwt.client.ui.CmsPushButton;
import org.opencms.gwt.client.ui.CmsScrollPanel;
import org.opencms.gwt.client.ui.I_CmsButton;
import org.opencms.gwt.client.ui.input.upload.CmsFileInfo;
import org.opencms.gwt.client.ui.input.upload.CmsFileInput;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget is used for importing aliases by uploading a CSV file.<p>
 * 
 * It contains buttons for uploading a file, a form used to submit the file to the server, and an area
 * used for displaying the result of the import operation on the server.<p>
 */
public class CmsImportView extends Composite {

    /**
     * The UiBinder interface for this widget.<p>
     */
    protected interface I_CmsImportViewUiBinder extends UiBinder<Widget, CmsImportView> {
        // empty
    }

    /** The actual form. */
    @UiField
    protected FormPanel m_formPanel;

    /** The panel containing the form elements. */
    @UiField
    protected FlowPanel m_formPanelContents;

    /** The label containing the path or name of the CSV file to import. */
    @UiField
    protected Label m_pathLabel;

    /** The panel containing the results of the server-side import operation. */
    @UiField
    protected CmsImportResultList m_results;

    /** The scroll panel containing the import results. */
    @UiField
    protected CmsScrollPanel m_scrollPanel;

    /** The button used to submit the file which should be imported to the server. */
    @UiField
    protected CmsPushButton m_submitButton;

    /** The upload button. */
    protected CmsUploadButton m_uploadButton;

    /**
     * Creates a new widget instance.<p>
     */
    public CmsImportView() {

        I_CmsImportViewUiBinder binder = GWT.create(I_CmsImportViewUiBinder.class);
        Widget content = binder.createAndBindUi(this);
        initWidget(content);
        I_CmsUploadButtonHandler handler = new I_CmsUploadButtonHandler() {

            public void initializeFileInput(CmsFileInput fileInput) {

                fileInput.setAllowMultipleFiles(false);
                fileInput.getElement().getStyle().setFontSize(200, Unit.PX);
                fileInput.setName(I_CmsAliasConstants.PARAM_IMPORTFILE);
                fileInput.addStyleName(org.opencms.ade.upload.client.ui.css.I_CmsLayoutBundle.INSTANCE.uploadCss().uploadFileInput());

            }

            public void onChange(CmsFileInput fileInput) {

                CmsFileInfo[] files = fileInput.getFiles();
                if (files.length == 1) {
                    CmsFileInfo fileInfo = files[0];
                    updatePath(fileInfo.getFileName());
                }

            }

            public void setButton(CmsUploadButton button) {

                // do nothing

            }

        };
        m_uploadButton = new CmsUploadButton(handler);
        m_formPanelContents.add(m_uploadButton);
        m_submitButton.setText(CmsAliasMessages.messageButtonSubmit());
        m_uploadButton.setText(CmsAliasMessages.messageButtonSelectFile());
        m_uploadButton.setSize(I_CmsButton.Size.big);
        m_submitButton.setSize(I_CmsButton.Size.big);

        m_submitButton.setEnabled(false);
        initializeForm();
    }

    /**
     * Shows a popup containing the import view.<p>
     */
    public static void showPopup() {

        final CmsPopup popup = new CmsPopup(CmsAliasMessages.messageTitleImport());
        CmsImportView importView = new CmsImportView();
        popup.setMainContent(importView);
        popup.setWidth(800);
        popup.addDialogClose(null);
        popup.centerHorizontally(100);
    }

    /**
     * The event handler for the submit button.<p>
     * 
     * @param event the click event 
     */
    @UiHandler("m_submitButton")
    public void onClickSubmit(ClickEvent event) {

        m_formPanel.submit();
    }

    /**
     * Adds an import result to the displayed list of import results.<p>
     * 
     * @param result the result to add 
     */
    protected void addImportResult(CmsClientAliasImportResult result) {

        String cssClass;
        I_Css css = CmsImportResultList.RESOURCES.css();
        switch (result.getStatus()) {
            case aliasChanged:
                cssClass = css.aliasImportOverwrite();
                break;
            case aliasError:
                cssClass = css.aliasImportError();
                break;
            case aliasNew:
            default:
                cssClass = css.aliasImportOk();
                break;
        }
        m_results.addRow(result.getLine(), result.getMessage(), cssClass);
    }

    /**
     * Clears the panel used to display the import results.<p>
     */
    protected void clearResults() {

        m_results.clear();
    }

    /** 
     * Processes the result of the import operation from the server.<p>
     * 
     * @param importResults the string containing the results of the import sent by the server
     */
    protected void handleImportResults(String importResults) {

        clearResults();
        JSONValue json = null;
        try {
            json = JSONParser.parseLenient(importResults);
        } catch (RuntimeException t) {
            CmsLog.log("Could not parse alias import results: '" + importResults + "'");
            throw t;
        }
        JSONObject jsonObj = (JSONObject)json;
        JSONValue resultVal = jsonObj.get(I_CmsAliasConstants.JSON_RESULT);
        JSONArray resultArray = (JSONArray)resultVal;
        List<CmsClientAliasImportResult> results = CmsClientAliasImportResult.parseArray(resultArray);
        for (CmsClientAliasImportResult singleResult : results) {
            addImportResult(singleResult);
        }
    }

    /**
     * Initializes the form used for submitting the alias CSV file to the server.<p>
     */
    protected void initializeForm() {

        String target = CmsSitemapView.getInstance().getController().getData().getAliasImportUrl();
        m_formPanel.setAction(target);
        m_formPanel.setMethod(FormPanel.METHOD_POST);
        m_formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
        Hidden siteRootField = new Hidden(I_CmsAliasConstants.PARAM_SITEROOT);
        siteRootField.setValue(CmsCoreProvider.get().getSiteRoot());
        m_formPanelContents.add(siteRootField);
        m_formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {

            public void onSubmitComplete(SubmitCompleteEvent event) {

                String results = event.getResults();
                handleImportResults(results);
                Timer resizeTimer = new Timer() {

                    @Override
                    public void run() {

                        m_scrollPanel.onResize();
                    }
                };
                resizeTimer.schedule(100);
            }

        });
    }

    /**
     * Updates the path of the file to import.<p>
     * 
     * @param path the new path 
     */
    protected void updatePath(String path) {

        m_pathLabel.setText(path);
        m_submitButton.setEnabled(true);
    }

}
