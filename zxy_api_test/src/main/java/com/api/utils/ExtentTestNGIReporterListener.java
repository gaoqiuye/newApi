package com.api.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;
import org.testng.xml.XmlSuite;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ResourceCDN;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.TestAttribute;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
//import com.aventstack.extentreports.reporter.configuration.ResourceCDN;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentTestNGIReporterListener extends TestListenerAdapter implements IReporter {
	// 生成的路径以及文件名
	private static final String OUTPUT_FOLDER = "test-output/";
	private static final String FILE_NAME = "index.html";
	private ExtentReports extent;
	private static Logger log = Logger.getLogger(ExtentTestNGIReporterListener.class);

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		init();
		boolean createSuiteNode = false;
		if (suites.size() > 1) {
			createSuiteNode = true;
		}
		for (ISuite suite : suites) {
			Map<String, ISuiteResult> result = suite.getResults();
			// 如果suite里面没有任何用例，直接跳过，不在报告里生成
			if (result.size() == 0) {
				continue;
			}
			// 统计suite下的成功、失败、跳过的总用例数
			int suiteFailSize = 0;
			int suitePassSize = 0;
			int suiteSkipSize = 0;
			ExtentTest suiteTest = null;
			// 存在多个suite的情况下，在报告中将同一个一个suite的测试结果归为一类，创建一级节点。
			if (createSuiteNode) {
				suiteTest = extent.createTest(suite.getName()).assignCategory(suite.getName());
			}
			boolean createSuiteResultNode = false;
			if (result.size() > 1) {
				createSuiteResultNode = true;
			}
			for (ISuiteResult r : result.values()) {
				ExtentTest resultNode;
				ITestContext context = r.getTestContext();
				if (createSuiteResultNode) {
					// 没有创建suite的情况下，将在SuiteResult的创建为一级节点，否则创建为suite的一个子节点。
					if (null == suiteTest) {
						resultNode = extent.createTest(r.getTestContext().getName());
					} else {
						resultNode = suiteTest.createNode(r.getTestContext().getName());
					}
				} else {
					resultNode = suiteTest;
				}
				if (resultNode != null) {
					resultNode.getModel().setName(suite.getName() + " : " + r.getTestContext().getName());
					if (resultNode.getModel().hasCategory()) {
						resultNode.assignCategory(r.getTestContext().getName());
					} else {
						resultNode.assignCategory(suite.getName(), r.getTestContext().getName());
					}
					resultNode.getModel().setStartTime(r.getTestContext().getStartDate());
					resultNode.getModel().setEndTime(r.getTestContext().getEndDate());
					// 统计SuiteResult下的数据
					int passSize = r.getTestContext().getPassedTests().size();
					int failSize = r.getTestContext().getFailedTests().size();
					int skipSize = r.getTestContext().getSkippedTests().size();
					suitePassSize += passSize;
					suiteFailSize += failSize;
					suiteSkipSize += skipSize;
					if (failSize > 0) {
						resultNode.getModel().setStatus(Status.FAIL);
					}
					resultNode.getModel().setDescription(
							String.format("Pass: %s ; Fail: %s ; Skip: %s ;", passSize, failSize, skipSize));
				}
				buildTestNodes(resultNode, context.getFailedTests(), Status.FAIL);
				buildTestNodes(resultNode, context.getSkippedTests(), Status.SKIP);
				buildTestNodes(resultNode, context.getPassedTests(), Status.PASS);
			}
			if (suiteTest != null) {
				suiteTest.getModel().setDescription(
						String.format("Pass: %s ; Fail: %s ; Skip: %s ;", suitePassSize, suiteFailSize, suiteSkipSize));
				if (suiteFailSize > 0) {
					suiteTest.getModel().setStatus(Status.FAIL);
				}
			}
		}
//        for (String s : Reporter.getOutput()) {
//            extent.setTestRunnerOutput(s);
//        }
		extent.flush();
	}

	private void init() {
		// 文件夹不存在的话进行创建
		File reportDir = new File(OUTPUT_FOLDER);
		if (!reportDir.exists() && !reportDir.isDirectory()) {
			reportDir.mkdir();
		}
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(OUTPUT_FOLDER + FILE_NAME);
		// 设置静态文件的DNS
		// 怎么样解决cdn.rawgit.com访问不了的情况
		htmlReporter.config().setResourceCDN(ResourceCDN.EXTENTREPORTS);
		htmlReporter.config().setDocumentTitle("api自动化测试报告");
		htmlReporter.config().setReportName("api自动化测试报告");
//		htmlReporter.config().setChartVisibilityOnOpen(true);
//        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
		htmlReporter.config().setTheme(Theme.STANDARD);
		htmlReporter.config().setEncoding("gbk");
		htmlReporter.config().setCSS(".node.level-1  ul{ display:none;} .node.level-1.active ul{display:block;}");
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setReportUsesManualConfiguration(true);
	}

	private void buildTestNodes(ExtentTest extenttest, IResultMap tests, Status status) {
		// 存在父节点时，获取父节点的标签
		String[] categories = new String[0];
		if (extenttest != null) {
			List<TestAttribute> categoryList = extenttest.getModel().getCategoryContext().getAll();
			categories = new String[categoryList.size()];
			for (int index = 0; index < categoryList.size(); index++) {
				categories[index] = categoryList.get(index).getName();
			}
		}
		ExtentTest test;
		if (tests.size() > 0) {
			// 调整用例排序，按时间排序
			Set<ITestResult> treeSet = new TreeSet<ITestResult>(new Comparator<ITestResult>() {
				@Override
				public int compare(ITestResult o1, ITestResult o2) {
					return o1.getStartMillis() < o2.getStartMillis() ? -1 : 1;
				}
			});
			treeSet.addAll(tests.getAllResults());
			for (ITestResult result : treeSet) {
				Object[] parameters = result.getParameters();
				// 如果有参数，则使用参数的toString组合代替报告中的name
				String name = parameters[14].toString() + " >>> " + parameters[15].toString() + " >>> 第"
						+ parameters[0].toString() + "条用例( " + parameters[1].toString() + " ) >>> 请求方法: ["
						+ parameters[5] + "] >>> 接口: " + parameters[2].toString();
				if (name.length() > 0) {
					if (name.contains("?")) {
						name = name.substring(0, name.indexOf("?"));
					}
				} else {
					name = result.getMethod().getMethodName();
				}
				if (extenttest == null) {
					test = extent.createTest(name);
				} else {
					// 作为子节点进行创建时，设置同父节点的标签一致，便于报告检索。
					test = extenttest.createNode(name).assignCategory(categories);
				}
				// test.getModel().setDescription(description.toString());
				// test = extent.createTest(result.getMethod().getMethodName());
				for (String group : result.getMethod().getGroups())
					test.assignCategory(group);

				List<String> outputList = Reporter.getOutput(result);
				for (String output : outputList) {
					// 将用例的log输出报告中
					test.debug(output);
				}
				if (result.getThrowable() != null) {
					test.log(status, result.getThrowable());
				} else {
					test.log(status, "Test " + status.toString().toLowerCase() + "ed");
				}
				test.getModel().setStartTime(getTime(result.getStartMillis()));
				test.getModel().setEndTime(getTime(result.getEndMillis()));
			}
		}
	}

	private Date getTime(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}

//	******TestListenerAdapter******
	@Override
	public void onTestStart(ITestResult result) {
		AssertsUtil.flag = true;
		AssertsUtil.errors.clear();
	}

	@Override
	public void onTestFailure(ITestResult tr) {
		this.handleAssertion(tr);
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		this.handleAssertion(tr);
	}

	@Override
	public void onTestSuccess(ITestResult tr) {
		this.handleAssertion(tr);
	}

	private int index = 0;

	private void handleAssertion(ITestResult tr) {
		if (!AssertsUtil.flag) {
			Throwable throwable = tr.getThrowable();
			if (throwable == null) {
				throwable = new Throwable();
			}
			StackTraceElement[] traces = throwable.getStackTrace();
			StackTraceElement[] alltrace = new StackTraceElement[0];
			for (Error e : AssertsUtil.errors) {
				StackTraceElement[] errorTraces = e.getStackTrace();
				StackTraceElement[] et = this.getKeyStackTrace(tr, errorTraces);
				StackTraceElement[] message = new StackTraceElement[] { new StackTraceElement(
						"message : " + e.getMessage() + " in method : ", tr.getMethod().getMethodName(),
						tr.getTestClass().getRealClass().getSimpleName(), index) };
				index = 0;
				alltrace = this.merge(alltrace, message);
				alltrace = this.merge(alltrace, et);
			}
			if (traces != null) {
				traces = this.getKeyStackTrace(tr, traces);
				alltrace = this.merge(alltrace, traces);
			}
			throwable.setStackTrace(alltrace);
			tr.setThrowable(throwable);
			AssertsUtil.flag = true;
			AssertsUtil.errors.clear();
			tr.setStatus(ITestResult.FAILURE);
		}
	}

	private StackTraceElement[] getKeyStackTrace(ITestResult tr, StackTraceElement[] stackTraceElements) {
		List<StackTraceElement> ets = new ArrayList<StackTraceElement>();
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			if (stackTraceElement.getClassName().equals(tr.getTestClass().getName())) {
				ets.add(stackTraceElement);
				index = stackTraceElement.getLineNumber();
			}
		}
		StackTraceElement[] et = new StackTraceElement[ets.size()];
		for (int i = 0; i < et.length; i++) {
			et[i] = ets.get(i);
		}
		return et;
	}

	private StackTraceElement[] merge(StackTraceElement[] traces1, StackTraceElement[] traces2) {
		StackTraceElement[] ste = new StackTraceElement[traces1.length + traces2.length];
		for (int i = 0; i < traces1.length; i++) {
			ste[i] = traces1[i];
		}
		for (int i = 0; i < traces2.length; i++) {
			ste[traces1.length + i] = traces2[i];
		}
		return ste;
	}
}
