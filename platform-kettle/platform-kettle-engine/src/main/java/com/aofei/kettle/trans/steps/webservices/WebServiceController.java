package com.aofei.kettle.trans.steps.webservices;

import com.aofei.kettle.PluginFactory;
import com.aofei.kettle.base.GraphCodec;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.webservices.WebServiceMeta;
import org.pentaho.di.trans.steps.webservices.wsdl.*;
import org.pentaho.di.trans.steps.webservices.wsdl.WsdlOpParameter.ParameterMode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.xml.namespace.QName;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;

@RestController
@RequestMapping(value="/WebServiceLookup")
@Api(tags = "Transformation转换 - Web服务 - 接口api")
public class WebServiceController {

	@ApiOperation(value = "加载WSDL地址或文件", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "graphXml", value = "转换的图形数据", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "stepName", value = "组件名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/loadWsdl")
	public List loadWebService(@RequestParam String graphXml, @RequestParam String stepName) throws Exception {
		ArrayList list = new ArrayList();

		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
		TransMeta transMeta = (TransMeta) codec.decode(graphXml);
		StepMeta stepMeta = transMeta.findStep(stepName);

		WebServiceMeta webServiceMeta = (WebServiceMeta) stepMeta.getStepMetaInterface();
		String anURI = transMeta.environmentSubstitute(webServiceMeta.getUrl());
		String proxyHost = webServiceMeta.getProxyHost();
		String proxyPort = webServiceMeta.getProxyPort();
		Wsdl wsdl = null;
		try {
			if (StringUtils.hasText(proxyHost) && StringUtils.hasText(proxyPort)) {
				Properties systemProperties = System.getProperties();
				systemProperties.setProperty("http.proxyHost", transMeta.environmentSubstitute(proxyHost));
				systemProperties.setProperty("http.proxyPort", transMeta.environmentSubstitute(proxyPort));
			}
			wsdl = new Wsdl(new URI(anURI), null, null, webServiceMeta.getHttpLogin(), webServiceMeta.getHttpPassword());
		} catch (AuthenticationException ae) {
			ae.printStackTrace();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}

		if (wsdl != null) {
			List<WsdlOperation> listeOperations = wsdl.getOperations();
			Collections.sort(listeOperations, new Comparator<WsdlOperation>() {
				public int compare(WsdlOperation op1, WsdlOperation op2) {
					return op1.getOperationQName().getLocalPart().compareTo(op2.getOperationQName().getLocalPart());
				}
			});
			for (Iterator<WsdlOperation> itr = listeOperations.iterator(); itr.hasNext();) {
				WsdlOperation op = itr.next();
				HashMap rec = new HashMap();
				rec.put("name", op.getOperationQName().getLocalPart());
				list.add(rec);
			}
		}

		return list;
	}

	@ApiOperation(value = "加载WebService中的方法", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "graphXml", value = "转换的图形数据", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "stepName", value = "组件名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping("/loadOperation")
	public WsOperationInfo loadOperation(@RequestParam String graphXml, @RequestParam String stepName) throws Exception {
//		HashMap operationInfo = new HashMap();
		WsOperationInfo operationInfo = new WsOperationInfo();

		Class PKG = WebServiceMeta.class;

		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
		TransMeta transMeta = (TransMeta) codec.decode(URLDecoder.decode(graphXml, "utf-8"));
		StepMeta stepMeta = transMeta.findStep(stepName);

		WebServiceMeta webServiceMeta = (WebServiceMeta) stepMeta.getStepMetaInterface();
		String anURI = transMeta.environmentSubstitute(webServiceMeta.getUrl());
		String proxyHost = webServiceMeta.getProxyHost();
		String proxyPort = webServiceMeta.getProxyPort();
		Wsdl wsdl = null;
		try {
			if (StringUtils.hasText(proxyHost) && StringUtils.hasText(proxyPort)) {
				Properties systemProperties = System.getProperties();
				systemProperties.setProperty("http.proxyHost", transMeta.environmentSubstitute(proxyHost));
				systemProperties.setProperty("http.proxyPort", transMeta.environmentSubstitute(proxyPort));
			}
			wsdl = new Wsdl(new URI(anURI), null, null, webServiceMeta.getHttpLogin(), webServiceMeta.getHttpPassword());
		} catch (AuthenticationException ae) {
			ae.printStackTrace();
			return operationInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return operationInfo;
		}


		if (wsdl != null) {
			WsdlParamContainer inWsdlParamContainer = null;
			WsdlParamContainer outWsdlParamContainer = null;
			WsdlOperation wsdlOperation = null;

			String anOperationName = webServiceMeta.getOperationName();
			Iterator<WsdlOperation> vItOperation = wsdl.getOperations().iterator();
			while (vItOperation.hasNext() && wsdlOperation == null) {
				WsdlOperation vCurrentOperation = vItOperation.next();
				if (vCurrentOperation.getOperationQName().getLocalPart().equals(anOperationName)) {
					wsdlOperation = vCurrentOperation;
				}
			}

			if (wsdlOperation != null) {
				String request = "";
				WsdlOpParameterList parameters = wsdlOperation.getParameters();
				if (parameters != null && parameters.getOperation() != null
						&& parameters.getOperation().getInput() != null
						&& parameters.getOperation().getInput().getName() != null) {
					request = wsdlOperation.getParameters().getOperation().getInput().getName().toString();
				}
				operationInfo.setRequestName(request);

				for (int cpt = 0; cpt < wsdlOperation.getParameters().size(); cpt++) {
					WsdlOpParameter param = wsdlOperation.getParameters().get(cpt);
					if (param.isArray()) {
						// setInFieldArgumentName(param.getName().getLocalPart());
						if (param.getItemXmlType() != null) {
							ComplexType type = param.getItemComplexType();
							if (type != null) {
								for (Iterator<String> itrType = type.getElementNames().iterator(); itrType.hasNext();) {
									String attributeName = itrType.next();
									QName attributeType = type.getElementType(attributeName);
									if (!WebServiceMeta.XSD_NS_URI.equals(attributeType.getNamespaceURI())) {
										throw new KettleStepException(BaseMessages.getString(PKG,
												"WebServiceDialog.ERROR0007.UnsupporteOperation.ComplexType"));
									}
								}
							}
							if (ParameterMode.IN.equals(param.getMode()) || ParameterMode.INOUT.equals(param.getMode())
									|| ParameterMode.UNDEFINED.equals(param.getMode())) {
								if (inWsdlParamContainer != null) {
									throw new KettleStepException(BaseMessages.getString(PKG,
											"WebServiceDialog.ERROR0006.UnsupportedOperation.MultipleArrays"));
								} else {
									inWsdlParamContainer = new WsdlOpParameterContainer(param);
								}
							} else if (ParameterMode.OUT.equals(param.getMode())
									|| ParameterMode.INOUT.equals(param.getMode())
									|| ParameterMode.UNDEFINED.equals(param.getMode())) {
								if (outWsdlParamContainer != null) {
									throw new KettleStepException(BaseMessages.getString(PKG,
											"WebServiceDialog.ERROR0006.UnsupportedOperation.MultipleArrays"));
								} else {
									outWsdlParamContainer = new WsdlOpParameterContainer(param);
								}
							}
						}
					} else {
						if (ParameterMode.IN.equals(param.getMode()) || ParameterMode.INOUT.equals(param.getMode())
								|| ParameterMode.UNDEFINED.equals(param.getMode())) {
							if (inWsdlParamContainer != null
									&& !(inWsdlParamContainer instanceof WsdlOperationContainer)) {
								throw new KettleStepException(BaseMessages.getString(PKG,
										"WebServiceDialog.ERROR0008.UnsupportedOperation.IncorrectParams"));
							} else {
								inWsdlParamContainer = new WsdlOperationContainer(wsdlOperation, param.getMode());
							}
						} else if (ParameterMode.OUT.equals(param.getMode())
								|| ParameterMode.INOUT.equals(param.getMode())
								|| ParameterMode.UNDEFINED.equals(param.getMode())) {
							if (outWsdlParamContainer != null
									&& !(outWsdlParamContainer instanceof WsdlOperationContainer)) {
								throw new KettleStepException(BaseMessages.getString(PKG,
										"WebServiceDialog.ERROR0008.UnsupportedOperation.IncorrectParams"));
							} else {
								outWsdlParamContainer = new WsdlOperationContainer(wsdlOperation, param.getMode());
							}
						} else {
							System.out.println("Parameter : " + param.getName().getLocalPart() + ", mode="
									+ param.getMode().toString() + ", is not considered");
						}
					}
				}
				if (wsdlOperation.getReturnType() != null) {
					WsdlOpParameter s = (WsdlOpParameter) wsdlOperation.getReturnType();
					outWsdlParamContainer = new WsdlOpParameterContainer(s);
					if (wsdlOperation.getReturnType().isArray()) {
						if (wsdlOperation.getReturnType().getItemXmlType() != null) {
							ComplexType type = wsdlOperation.getReturnType().getItemComplexType();
							if (type != null) {
								for (Iterator<String> itrType = type.getElementNames().iterator(); itrType.hasNext();) {
									String attributeName = itrType.next();
									QName attributeType = type.getElementType(attributeName);
									if (!WebServiceMeta.XSD_NS_URI.equals(attributeType.getNamespaceURI())) {
										throw new KettleStepException(BaseMessages.getString(PKG,
												"WebServiceDialog.ERROR0007.UnsupportedOperation.ComplexType"));
									}
								}
							}
						}
					}
				}
			}

			WsOperationParam paramIn = new WsOperationParam();
			operationInfo.setInParam(paramIn);
			if(inWsdlParamContainer != null) {
				String containerName = inWsdlParamContainer.getContainerName();
				paramIn.setTitle( containerName == null ? "in" : containerName);
				operationInfo.setInWsdlParamContainer(true);

				String[] params = inWsdlParamContainer.getParamNames();
				for (int cpt = 0; cpt < params.length; cpt++) {
					paramIn.addParam(params[cpt], inWsdlParamContainer.getParamType(params[cpt]));
				}

			} else {
				String inFieldContainerName = webServiceMeta.getInFieldContainerName();
				paramIn.setTitle( inFieldContainerName == null ? "in" : inFieldContainerName);
			}


			WsOperationParam paramOut = new WsOperationParam();
			operationInfo.setOutParam(paramOut);

			if(outWsdlParamContainer != null) {
				String containerName = outWsdlParamContainer.getContainerName();
				paramOut.setTitle( containerName == null ? "out" : containerName);
				operationInfo.setOutWsdlParamContainer(true);

				String[] params = outWsdlParamContainer.getParamNames();
				for (int cpt = 0; cpt < params.length; cpt++) {
					paramOut.addParam(params[cpt], outWsdlParamContainer.getParamType(params[cpt]));
				}

			} else {
				String outFieldContainerName = webServiceMeta.getOutFieldContainerName();
				paramOut.setTitle( outFieldContainerName == null ? "in" : outFieldContainerName);
			}

		}

		return operationInfo;
	}

}
