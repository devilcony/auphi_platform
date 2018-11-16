package com.aofei.kettle.trans.steps.webservices;

public class WsOperationInfo {

	private String operationName;

	private String requestName;

	private WsOperationParam inParam;

	private WsOperationParam outParam;

	private Boolean inWsdlParamContainer = false;
	private Boolean outWsdlParamContainer = false;

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	/**
	 * @return the inParam
	 */
	public WsOperationParam getInParam() {
		return inParam;
	}

	/**
	 * @param inParam the inParam to set
	 */
	public void setInParam(WsOperationParam inParam) {
		this.inParam = inParam;
	}

	/**
	 * @return the outParam
	 */
	public WsOperationParam getOutParam() {
		return outParam;
	}

	/**
	 * @param outParam the outParam to set
	 */
	public void setOutParam(WsOperationParam outParam) {
		this.outParam = outParam;
	}

	/**
	 * @return the inWsdlParamContainer
	 */
	public Boolean getInWsdlParamContainer() {
		return inWsdlParamContainer;
	}

	/**
	 * @param inWsdlParamContainer the inWsdlParamContainer to set
	 */
	public void setInWsdlParamContainer(Boolean inWsdlParamContainer) {
		this.inWsdlParamContainer = inWsdlParamContainer;
	}

	/**
	 * @return the outWsdlParamContainer
	 */
	public Boolean getOutWsdlParamContainer() {
		return outWsdlParamContainer;
	}

	/**
	 * @param outWsdlParamContainer the outWsdlParamContainer to set
	 */
	public void setOutWsdlParamContainer(Boolean outWsdlParamContainer) {
		this.outWsdlParamContainer = outWsdlParamContainer;
	}


}
