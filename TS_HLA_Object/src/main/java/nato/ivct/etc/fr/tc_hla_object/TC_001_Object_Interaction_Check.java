/*
Copyright 2017, FRANCE (DGA/Capgemini)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package nato.ivct.etc.fr.tc_hla_object;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
import de.fraunhofer.iosb.tc_lib.IVCT_LoggingFederateAmbassador;
import de.fraunhofer.iosb.tc_lib.IVCT_RTI_Factory;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcFailed;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import hla.rti1516e.FederateHandle;
import nato.ivct.etc.fr.tc_lib_hla_object.HLA_Object_BaseModel;
import nato.ivct.etc.fr.tc_lib_hla_object.HLA_Object_TcParam;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Constant;
import nato.ivct.etc.fr.fctt_common.utils.TextInternationalization;

import org.slf4j.Logger;

import java.io.File;

/**
 * @author FRANCE (DGA/Capgemini)
 */
public class TC_001_Object_Interaction_Check extends AbstractTestCase {
    FederateHandle                              TcFederateHandle;
    private String                              TcFederateName = "IVCT_HLA_Object";

    // Build test case parameters to use
    static HLA_Object_TcParam              		HlaObjectTcParam;

    // Get logging-IVCT-RTI using tc_param federation name, host
    private static IVCT_RTIambassador           ivct_rti;
    static HLA_Object_BaseModel            		HlaObjectBaseModel;
    
    static IVCT_LoggingFederateAmbassador		ivct_LoggingFederateAmbassador;

    
    @Override
    public IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger) throws TcInconclusive {

    	try {
	    	HlaObjectTcParam           		= new HLA_Object_TcParam(tcParamJson);
	    	ivct_rti                        = IVCT_RTI_Factory.getIVCT_RTI(logger);
	    	HlaObjectBaseModel         		= new HLA_Object_BaseModel(logger, ivct_rti, HlaObjectTcParam);
	    	ivct_LoggingFederateAmbassador  = new IVCT_LoggingFederateAmbassador(HlaObjectBaseModel, logger);
    	}
    	catch(Exception ex) {
    		logger.error(TextInternationalization.getString("etc_fra.noInstanciation"));
    	}
    	return HlaObjectBaseModel;
    }

    @Override
    protected void logTestPurpose(final Logger logger) {
    	
    	// Build purpose text
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FCTT_Constant.REPORT_FILE_SEPARATOR);
        stringBuilder.append(TextInternationalization.getString("etc_fra.purpose")); stringBuilder.append("\n");
        stringBuilder.append(TextInternationalization.getString("hlaobject.objectsPublication")); stringBuilder.append("\n");
        stringBuilder.append(TextInternationalization.getString("hlaobject.FomSomComparison1")); stringBuilder.append("\n");
        stringBuilder.append(TextInternationalization.getString("hlaobject.FomSomComparison2")); stringBuilder.append("\n");
        final String testPurpose = stringBuilder.toString();
        logger.info(testPurpose);
    }


    @Override
    protected void preambleAction(final Logger logger) throws TcInconclusive {

    	// Load FOM/SOM files
        if (HlaObjectBaseModel.loadFomSomFiles() == false)
        	throw new TcInconclusive(TextInternationalization.getString("etc_fra.FomSomError"));
        
    	// Initiate rti
        TcFederateHandle = HlaObjectBaseModel.initiateRti(TcFederateName, ivct_LoggingFederateAmbassador);

        // Do the necessary calls to get handles and do publish and subscribe
        if (HlaObjectBaseModel.init(HlaObjectTcParam.getSutName()))
            throw new TcInconclusive(TextInternationalization.getString("etc_fra.initError"));

    	logger.info(TextInternationalization.getString("etc_fra.RtiConnected"));
    	logger.info(FCTT_Constant.REPORT_FILE_SEPARATOR);
    }


    @Override
    protected void performTest(final Logger logger) throws TcInconclusive, TcFailed {

    	// Check result directory
    	String resultFileName = HlaObjectTcParam.getResultDir();
        File resultFile = new File(resultFileName);
        if (!resultFile.exists())
            throw new TcInconclusive(String.format(TextInternationalization.getString("etc_fra.resultDirError"),resultFileName));
    	
        // Allow time to work and get some reflect values.
        if (HlaObjectBaseModel.sleepFor(logger,HlaObjectTcParam.getTestDuration())) {
            throw new TcInconclusive(TextInternationalization.getString("etc_fra.sleepError"));
        }
        
    	logger.info(TextInternationalization.getString("etc_fra.wakeup"));

    	// Generate result files
        if (HlaObjectBaseModel.validateObjects() == false)
        	throw new TcFailed(TextInternationalization.getString("hlaobject.invalidObjects"));
    }


    @Override
    protected void postambleAction(final Logger logger) throws TcInconclusive, TcInconclusive {
        
        // Terminate rti
        HlaObjectBaseModel.terminateRti();
    }
}
