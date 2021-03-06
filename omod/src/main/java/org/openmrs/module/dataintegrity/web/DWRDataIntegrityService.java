/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.dataintegrity.web;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataintegrity.DataIntegrityConstants;
import org.openmrs.module.dataintegrity.DataIntegrityService;
import org.openmrs.module.dataintegrity.IntegrityCheck;
import org.openmrs.module.dataintegrity.IntegrityCheckColumn;
import org.openmrs.module.dataintegrity.IntegrityCheckResult;
import org.openmrs.module.dataintegrity.IntegrityCheckRun;
import org.openmrs.module.dataintegrity.QueryResults;
import org.openmrs.web.dwr.DWRException;

/**
 * Service to provide DWR access to data integrity api calls
 */
public class DWRDataIntegrityService {

	private final Log log = LogFactory.getLog(getClass());

	/**
	 * tests sql code, setting a limit on the number of responses one should expect back
	 * 
	 * @param code the sql to be run
	 * @param limit the hard limit on the amount of records to be returned
	 * @return a QueryResults object containing the results
	 * @throws DWRException 
	 */
	public DWRQueryResults testCode(String code, Integer limit) throws DWRException {
		DataIntegrityService service = Context.getService(DataIntegrityService.class);

		if (StringUtils.isEmpty(code)) {
			throw new DWRException("code was empty");
		}

		// trim it up
		code = code.trim();

		log.info("executing code: " + code);

		QueryResults qr = service.getQueryResults(code, limit);
		if (qr == null) {
			return null;
		}
		return new DWRQueryResults(qr);
	}

	/**
	 * finds columns for a query by running code and extracting them.
	 * 
	 * @param code the SQL code to run
	 * @return a list of columns based on the results
	 * @throws DWRException 
	 */
	public DWRIntegrityCheckColumns getColumnsFromCode(String code) throws DWRException {
		DWRQueryResults dqr = testCode(code, 0);
		List<IntegrityCheckColumn> results = new ArrayList<IntegrityCheckColumn>();
		for (String column: dqr.getColumns()) {
			IntegrityCheckColumn col = new IntegrityCheckColumn();
			col.setShowInResults(Boolean.TRUE);
			col.setUsedInUid(Boolean.FALSE);
			col.setName(column);
			col.setDisplayName(column);
			results.add(col);
		}
		return new DWRIntegrityCheckColumns(results);
	}
	
	/**
	 * runs an integrity check and returns the results.
	 * 
	 * @param checkId the integrity check to be run
	 * @return the resulting integrity check run
	 * @throws Exception 
	 */
	public IntegrityCheckRun runIntegrityCheck(Integer checkId) throws Exception {
		DataIntegrityService service = Context.getService(DataIntegrityService.class);

		IntegrityCheck check = service.getIntegrityCheck(checkId);
		if (check != null) {
			// if there is an exception, it will be thrown back to the browser
			IntegrityCheckRun run = service.runIntegrityCheck(check);
			return run;
		}
		
		// nothing happened, so return a blank string
		return null;
	}
	
	/**
	 * ignores or un-ignores a result based on current status.
	 * 
	 * @param checkId the related integrity check
	 * @param uid the uid of the record to be modified
	 * @param status the current status of the result
	 * @return success
	 */
	public Boolean ignoreResult(Integer checkId, String uid, Integer status) {
		DataIntegrityService service = Context.getService(DataIntegrityService.class);
		if (checkId == null || StringUtils.isEmpty(uid))
			return false;
		
		IntegrityCheck check = service.getIntegrityCheck(checkId);
		if (check == null)
			return false;
		
		IntegrityCheckResult result = service.findResultForIntegrityCheckByUid(check, uid);
		if (result == null)
			return false;
		
		result.setStatus(DataIntegrityConstants.RESULT_STATUS_NEW.equals(status) ? 
				DataIntegrityConstants.RESULT_STATUS_IGNORED :
				DataIntegrityConstants.RESULT_STATUS_NEW);
		service.saveIntegrityCheck(check);
		return true;
	}
}
