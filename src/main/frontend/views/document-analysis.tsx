import { Button } from '@vaadin/react-components/Button';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { FormLayout } from '@vaadin/react-components/FormLayout';
import { TextField } from '@vaadin/react-components/TextField';
import InvoiceRecordModel from "Frontend/generated/com/dev/sagar/services/documentanalysis/InvoiceRecordModel";
import {useForm} from "@vaadin/hilla-react-form";
import { DatePicker } from '@vaadin/react-components/DatePicker';
import { NumberField } from '@vaadin/react-components/NumberField';
import { HorizontalLayout } from '@vaadin/react-components/HorizontalLayout';
import { VerticalLayout } from '@vaadin/react-components/VerticalLayout';
import { Upload } from '@vaadin/react-components/Upload';
import {useState, useRef, useEffect} from "react";
import {UploadElement} from "@vaadin/react-components";
import InvoiceRecord from "Frontend/generated/com/dev/sagar/services/documentanalysis/InvoiceRecord";

export const config: ViewConfig = {
  menu: { order: 1, icon: 'line-awesome/svg/file-pdf-solid.svg' },
  title: 'Analyse Document',
};

export default function AnalyseDocumentView() {

    const [disabled, setDisabled] = useState<boolean>(true);
    const uploadRef = useRef<UploadElement>(null);

    const responsiveSteps = [
        { minWidth: '0', columns: 1 },
        { minWidth: '500px', columns: 2 },
    ];

    const {read, model, field, clear} = useForm(InvoiceRecordModel);

      async function analyzeInvoice() {
        console.log("Uploading file...", uploadRef.current?.files[0]);
        uploadRef.current?.uploadFiles();
        uploadRef.current?.addEventListener('upload-success', (e) => {
            console.log("File uploaded successfully");
            const response : InvoiceRecord = JSON.parse(e.detail.xhr.response);
            //console.log("Server response: ", response);
            read(response);
            setDisabled(false);
        });
      }

      function resetForm() {
        setDisabled(true);
        clear();
        const removeButton = uploadRef.current?.
                                    querySelector("#outlet > vaadin-app-layout > vaadin-horizontal-layout > vaadin-vertical-layout > vaadin-upload > vaadin-upload-file-list > li > vaadin-upload-file")?.
                                    shadowRoot?.querySelector("div > div:nth-child(2) > button:nth-child(3)")
        //console.log("Remove button: ", removeButton);
        if (removeButton instanceof HTMLElement) {
            removeButton.click();
        }
      }


    useEffect(() => {
        if (!uploadRef.current) {
            return;
        }
        uploadRef.current.i18n.addFiles.one = 'Select File...';
        uploadRef.current.maxFiles = 1;
        uploadRef.current.i18n = { ...uploadRef.current.i18n };
    }, []);

  return (
    <>
        <HorizontalLayout theme="spacing padding"
                          className="height-4xl"
                          style={{ justifyContent: 'center', width: '100%' }}>
            <FormLayout responsiveSteps={responsiveSteps}>
                <TextField label="Customer Name" {...field(model.customerName)} disabled={disabled}/>
                <TextField label="Vendor name" {...field(model.vendorName)} disabled={disabled}/>
                <TextField label="Invoice Id" {...field(model.invoiceNumber)} disabled={disabled}/>
                <DatePicker label="Date" {...field(model.invoiceData)} disabled={disabled}/>
                <NumberField label="Invoice Total Amount" {...field(model.invoiceTotal)} disabled={disabled}/>
            </FormLayout>
            <VerticalLayout theme="spacing padding"
                            className="height-4xl"
                            style={{ justifyContent: 'center'}}>
                <Upload noAuto ref={uploadRef} target="/api/fileupload" accept="application/pdf" />
                <Button theme="primary" onClick={analyzeInvoice} className="self-center" disabled={!disabled}>Analyze Document</Button>
                <Button theme="secondary" onClick={resetForm} className="self-center">Clear</Button>
            </VerticalLayout>
        </HorizontalLayout>
    </>
  );
}
