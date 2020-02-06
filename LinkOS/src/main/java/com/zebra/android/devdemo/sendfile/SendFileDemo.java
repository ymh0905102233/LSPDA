/***********************************************
 * CONFIDENTIAL AND PROPRIETARY 
 * 
 * The source code and other information contained herein is the confidential and the exclusive property of
 * ZIH Corp. and is subject to the terms and conditions in your end user license agreement.
 * This source code, and any other information contained herein, shall not be copied, reproduced, published, 
 * displayed or distributed, in whole or in part, in any medium, by any means, for any purpose except as
 * expressly permitted under such license agreement.
 * 
 * Copyright ZIH Corp. 2012
 * 
 * ALL RIGHTS RESERVED
 ***********************************************/

package com.zebra.android.devdemo.sendfile;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;

import com.zebra.android.devdemo.ConnectionScreen;
import com.zebra.android.devdemo.util.SettingsHelper;
import com.zebra.android.devdemo.util.UIHelper;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SendFileDemo extends ConnectionScreen {

    private UIHelper helper = new UIHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testButton.setText("Send Test File");
    }

    @Override
    public void performTest() {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                sendFile();
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();

    }

    private void sendFile() {
        Connection connection = null;
        if (isBluetoothSelected() == true) {
            connection = new BluetoothConnection(getMacAddressFieldText());
        } else {
            try {
                int port = Integer.parseInt(getTcpPortNumber());
                connection = new TcpConnection(getTcpAddress(), port);
            } catch (NumberFormatException e) {
                helper.showErrorDialogOnGuiThread("Port number is invalid");
                return;
            }
        }
        try {
            helper.showLoadingDialog("Sending file to printer ...");
            connection.open();
            ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
            testSendFile(printer);
            connection.close();
        } catch (ConnectionException e) {
            helper.showErrorDialogOnGuiThread(e.getMessage());
        } catch (ZebraPrinterLanguageUnknownException e) {
            helper.showErrorDialogOnGuiThread(e.getMessage());
        } finally {
            helper.dismissLoadingDialog();
        }
    }

    private void testSendFile(ZebraPrinter printer) {
        try {
            File filepath = getFileStreamPath("TEST.LBL");
            createDemoFile(printer, "TEST.LBL");
            printer.sendFileContents(filepath.getAbsolutePath());
            SettingsHelper.saveBluetoothAddress(this, getMacAddressFieldText());
            SettingsHelper.saveIp(this, getTcpAddress());
            SettingsHelper.savePort(this, getTcpPortNumber());

        } catch (ConnectionException e1) {
            helper.showErrorDialogOnGuiThread("Error sending file to printer");
        } catch (IOException e) {
            helper.showErrorDialogOnGuiThread("Error creating file");
        }
    }

    private void createDemoFile(ZebraPrinter printer, String fileName) throws IOException {

        FileOutputStream os = this.openFileOutput(fileName, Context.MODE_PRIVATE);

        byte[] configLabel = null;

        PrinterLanguage pl = printer.getPrinterControlLanguage();
        pl = PrinterLanguage.CPCL;
        if (pl == PrinterLanguage.ZPL) {
            configLabel = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ".getBytes();
            String zpl = "^XA\n" +
                    "^MMT\n" +
                    "^PW1122\n" +
                    "^LL0709\n" +
                    "^LS0\n" +
                    "^FT101,292^A0N,42,40^FH\\^FDClass:^FS\n" +
                    "^FT215,294^A0N,42,24^FH\\^FD1^FS\n" +
                    "^FO494,254^GB559,51,2^FS\n" +
                    "^FT505,294^A0N,46,45^FH\\^FDPRODUCE OF NEW ZEALAND^FS\n" +
                    "^FT97,360^A0N,42,40^FH\\^FDVariety:^FS\n" +
                    "^FT95,434^A0N,42,40^FH\\^FDCount of  punnet:^FS\n" +
                    "^FT817,434^A0N,42,40^FH\\^FDSize\\9C\\00^FS\n" +
                    "^FT93,521^A0N,42,40^FH\\^FDZ^FS\n" +
                    "^FT155,519^A0N,42,40^FH\\^FDCK^FS\n" +
                    "^FT233,521^A0N,42,40^FH\\^FDPDQ^FS\n" +
                    "^FT341,520^A0N,42,40^FH\\^FDJB^FS\n" +
                    "^FT559,530^A0N,42,40^FH\\^FDSource Pallet:^FS\n" +
                    "^BY4,3,137^FT130,187^BCN,,Y,N\n" +
                    "^FD>;@CartonCode^FS\n" +
                    "^FT239,362^A0N,42,40^FH\\^FDHayward Green^FS\n" +
                    "^FT411,435^A0N,42,40^FH\\^FD9^FS\n" +
                    "^FT947,431^A0N,42,40^FH\\^FD18^FS\n" +
                    "^FT817,529^A0N,42,40^FH\\^FD1102^FS\n" +
                    "^PQ1,0,1,Y\n" +
                    "^XZ";

            configLabel=zpl.getBytes();
        } else if (pl == PrinterLanguage.CPCL) {
//            String cpclConfigLabel = "! 0 200 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 TEST\r\n" + "PRINT\r\n";
            String cpclConfigLabel = "! 0 200 200 1838 1\n" +
                    "PW 799\n" +
                    "LABEL\n" +
                    "GAP-SENSE\n" +
                    "T90 4 0 153 1799 (00)294158350019759765\n" +
                    "BT OFF\n" +
                    "VB 128 2 30 71 63 1791 00294158350019759765\n" +
                    "BOX 64 348 607 735 2\n" +
                    "BOX 423 1261 748 1808 2\n" +
                    "BOX 225 752 606 1245 2\n" +
                    "BOX 226 1260 407 1809 2\n" +
                    "BOX 64 66 351 144 2\n" +
                    "BOX 66 224 352 303 2\n" +
                    "BOX 67 753 195 1256 2\n" +
                    "L 215 756 215 1801 1\n" +
                    "L 586 1264 586 1799 1\n" +
                    "BOX 621 754 749 1091 2\n" +
                    "BOX 621 1097 748 1247 2\n" +
                    "BOX 625 342 748 735 2\n" +
                    "T90 4 6 154 592 30\n" +
                    "L 667 1265 667 1797 1\n" +
                    "T90 5 0 529 1206 45316921\n" +
                    "T90 5 0 376 1211 45316919\n" +
                    "L 506 1424 506 1802 1\n" +
                    "T180 4 0 304 129 19759769\n" +
                    "L 590 1340 747 1340 1\n" +
                    "T180 4 0 300 288 19759769\n" +
                    "T90 4 3 85 1213 19759769\n" +
                    "T90 5 0 567 863 19407\n" +
                    "T90 5 0 455 1210 45316920\n" +
                    "T90 5 0 529 1071 0000050817007\n" +
                    "T90 5 0 414 865 19400\n" +
                    "T90 5 0 455 1067 0000050817006\n" +
                    "T90 5 0 302 1212 45316918\n" +
                    "L 427 1424 750 1424 1\n" +
                    "T90 5 0 376 1073 0000050817005\n" +
                    "T90 5 0 303 1069 0000050817004\n" +
                    "L 428 1508 750 1508 1\n" +
                    "T90 5 0 604 1678 Brand\n" +
                    "T90 5 0 604 1786 Orign\n" +
                    "T90 5 0 694 1198 Qty\n" +
                    "T90 5 0 247 1241 Source Pallet:\n" +
                    "T90 5 0 529 863 19403\n" +
                    "T90 5 0 376 865 19398\n" +
                    "L 425 1592 747 1592 1\n" +
                    "T90 5 0 655 1204 Pallet\n" +
                    "T90 5 0 535 1473 C\n" +
                    "T90 5 0 698 1313 GR\n" +
                    "T90 5 0 494 863 19402\n" +
                    "T90 5 0 341 865 19397\n" +
                    "T90 5 0 247 888 Growers\n" +
                    "T90 5 0 247 1083 CIQ Contact NO.\n" +
                    "T90 5 0 535 1563 IT\n" +
                    "L 624 934 745 934 1\n" +
                    "T90 5 0 452 863 19401\n" +
                    "T90 5 0 299 865 19396\n" +
                    "T90 5 0 699 1393 CK\n" +
                    "L 424 1699 746 1699 1\n" +
                    "T90 5 0 698 1477 88\n" +
                    "T90 5 0 696 1567 GA\n" +
                    "T90 5 0 698 1652 Z\n" +
                    "T90 5 0 638 1422 Method\n" +
                    "T90 5 0 456 1495 Make\n" +
                    "T90 5 0 698 1772 NZ\n" +
                    "T90 4 3 465 1396 30\n" +
                    "T90 5 0 604 1589 Variety\n" +
                    "T90 5 0 535 1653 N\n" +
                    "T90 5 0 349 1502 2019-06-01\n" +
                    "T90 5 0 703 875 168\n" +
                    "L 673 754 673 1088 2\n" +
                    "T90 5 0 635 1056 PACK:\n" +
                    "T90 5 0 635 899 Punnet:\n" +
                    "T90 5 0 305 1504 Delivery Date:\n" +
                    "T90 5 0 353 1788 TST19021801\n" +
                    "T90 5 0 535 1767 M\n" +
                    "T90 5 0 634 1319 Ind\n" +
                    "T90 5 0 702 1039 21\n" +
                    "L 292 1084 596 1084 1\n" +
                    "T90 5 0 305 1793 Customer PO:\n" +
                    "L 295 769 592 769 1\n" +
                    "L 290 892 593 892 1\n" +
                    "T90 5 0 456 1780 Base\n" +
                    "T90 5 0 256 1791 Customer:\n" +
                    "T90 5 0 256 1660 0003007522\n" +
                    "T90 5 0 256 1506 SO:\n" +
                    "T90 5 0 256 1441 0000534427\n" +
                    "T90 5 0 456 1576 Style\n" +
                    "T90 5 0 674 548 011515\n" +
                    "T90 5 0 604 1493 Class\n" +
                    "T90 5 0 604 1411 Grow\n" +
                    "T90 5 0 604 1328 Label\n" +
                    "T90 5 0 456 1674 Stack\n" +
                    "T90 5 0 674 722 Pack detail:\n" +
                    "T180 5 0 667 228 (00)294158350019759765\n" +
                    "T180 5 0 663 72 (00)294158350019759765\n" +
                    "B 128 1 30 56 700 144 00294158350019759765\n" +
                    "B 128 1 30 58 700 303 00294158350019759765\n" +
                    "L 290 769 290 1234 1\n" +
                    "L 594 770 594 1233 1\n" +
                    "L 292 1232 594 1232 1\n" +
                    "FORM\n" +
                    "PRINT\n";
            configLabel = cpclConfigLabel.getBytes();
        }
        os.write(configLabel);
        os.flush();
        os.close();
    }

}
