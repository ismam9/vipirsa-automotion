package vipirsa.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * Clase que contiene la configuración base para iniciar la automatización:
 * carga los datos de los properties,
 * define las capabilities, genera el objeto con el driver que corresponda
 * dependiendo del navegador a utilizar, configura la creacion del reporte de
 * resultados y cierra el driver de automatización al finalizar el test.
 */

public class GridConfig{

	public static WebDriver driver = null;
	public static Properties config = new Properties();
	public static Properties client = new Properties();
	public static Properties otp = new Properties();

	public static Log log;
	public static WebActions wa;

	// Reporte
	public static ExtentHtmlReporter htmlReporter;
	public static ExtentReports extent;
	public static ExtentTest test;

	public static DataFormatter formatter = new DataFormatter();
	
	public String nombreClase;
	public String[] splitString;
	
	public String dlPath;
	public static String entorno;
	public static String measurementName;
	public static String suiteName;

	/**
	 * Método que recibe los parametros desde la Suite para generar el objeto driver
	 * donde se ejecutará el test.
	 * 
	 * @param browser  Indica el tipo de navegador donde se ejecutará el test.
	 * @param platform Indica el sistema operativo del equipo.
	 */
	@Parameters({ "browser", "platform", "port", "dataname", "suitname"})
	@BeforeMethod(alwaysRun = true)
	public void setupDriver(String browser, String platform, @Optional("OptParam1") String port, @Optional("OptParam2") String dataname, @Optional("OptParam3") String suitname) {
		
		//String remoteUrl = "";
		entorno = "";
		measurementName = dataname;
		suiteName = suitname;
		 
		try {
			DesiredCapabilities caps = new DesiredCapabilities();
			try {
				config.load(new FileInputStream("config/config.properties"));
				//client.load(new FileInputStream("datos/client.properties"));
				//otp.load(new FileInputStream("config/otp.properties"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			wa = new WebActions();
			log = new Log();

			// Driver según el browser
			if (browser.equalsIgnoreCase("chrome") && platform.equalsIgnoreCase("windows")) {
				
				caps.setPlatform(Platform.WINDOWS);
				
				System.setProperty("webdriver.chrome.driver", config.getProperty("chromePath"));

				Map<String, Object> prefs = new HashMap<String, Object>();

				// Use File.separator as it will work on any OS
				prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + "descargas");

				// Adding capabilities to ChromeOptions
				ChromeOptions options = new ChromeOptions();
				options.setExperimentalOption("prefs", prefs);
				
				dlPath = System.getProperty("user.dir") + File.separator + "descargas";
				entorno = platform;

				// Launching browser with desired capabilities
				driver = new ChromeDriver(options);

			
			} else {
				System.setProperty("webdriver.gecko.driver", config.getProperty("firefoxPath"));
				caps = DesiredCapabilities.firefox();
				driver = new FirefoxDriver();
			}

			// setupReport(platform, browser);
			driver.manage().window().maximize();

//			driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);

		} catch (Exception ex) {
			System.out.println("Operación fallida. No se ha podido configurar el entorno.");
			System.out.println("Mensaje de error: " + ex.getMessage());
			//JOptionPane.showMessageDialog(null, "Operación fallida. No se ha podido configurar el entorno.");
		}

	}
	
	//Este metodo crea la configuracion necesaria para que el driver obtenga los datos del portapapeles desde el navegador
	//SettingValue: 0 es valor por defecto; 1 habilitar; 2 deshabilitar
	private static Map<String,Object> getClipBoardSettingsMap(int settingValue) throws JsonProcessingException {
	    Map<String,Object> map = new HashMap<>();
	    map.put("last_modified",String.valueOf(System.currentTimeMillis()));
	    map.put("setting", settingValue);
	    Map<String,Object> cbPreference = new HashMap<>();
	    cbPreference.put("[*.],*",map);
	    ObjectMapper objectMapper = new ObjectMapper();
	    String json = objectMapper.writeValueAsString(cbPreference);
	    System.out.println("clipboardSettingJson: " + json);
	    return cbPreference;
	}
	
	/**
	 * Método que inicia la creación de un reporte de resultados al ejecutar el
	 * test.
	 * 
	 * @param platformName Indica el sistema operativo del equipo.
	 * @param browser      Indica el tipo de navegador donde se ejecutará el test.
	 */
	@Parameters({ "browser", "platform", "suitname" })
	@BeforeTest
	public void setupReport(String browser, String platform, String suitname) {
		File imgPostman = new File(getClass().getClassLoader().getResource("logo.jpg").getFile().replaceAll("%20", " "));
		File imgCliente = new File(
				getClass().getClassLoader().getResource("logoEmpresa.jpg").getFile().replaceAll("%20", " "));
		String rutaImgPostman = imageToBase64(imgPostman);
		String rutaImgEmpresa = imageToBase64(imgCliente);
		
		String childTags = "<img src=\"data:image/jpg;base64, " + rutaImgPostman + "\" alt=\"Logo MTP\" class=\"logo-mtp\">"
                +  "<a class=\"bloque\" href=http://xxxx.xxxx.xxx/reportesWeb/><img src=\"data:image/jpg;base64, " + rutaImgEmpresa + "\" alt=\"Logo Postman\" class=\"logo-empresa\"></a>"
                +  "<span class=\"app-version\">Entorno Local Web</span>";
		String titulo = "<span class=\"custom-title\">" + childTags + "</span>";

		SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
		Date fecha = new Date();

		htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + File.separator + "reportesHTML" + 
		File.separator +formatoFecha.format(fecha)+ File.separator + "Reporte " + suitname + " " +formato.format(fecha)+".html");
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setSystemInfo("OS", platform);
		extent.setSystemInfo("Browser", browser);

		// configuration items to change the look and feel
		// add content, manage tests etc
		htmlReporter.config().setDocumentTitle("Reporte Vipirsa");
		htmlReporter.config().setReportName(titulo);
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setTheme(Theme.STANDARD);
		htmlReporter.config().setTimeStampFormat("dd-MM-yyyy, hh:mm a");
		htmlReporter.loadXMLConfig(System.getProperty("user.dir") + File.separator + "extent-config.xml");
				
	}

	/**
	 * Método que convierte un archivo de imagen en una cadena de Base64.
	 * 
	 * @param imageFile fichero de imagen a convertir.
	 */
	public String imageToBase64(File imageFile) {

		try {
			System.out.println(imageFile.getPath());
			FileInputStream fis = new FileInputStream(imageFile);
			byte[] bytes = new byte[(int) imageFile.length()];
			fis.read(bytes);
			String base64Img = new String(Base64.encodeBase64(bytes), "UTF-8");
			return base64Img;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al convertir la imagen a Base 64");
			return "";
		}

	}

	/**
	 * Método que registra en el reporte el resultado de un test.
	 * 
	 * @param result estado del test ejecutado.
	 * @throws IOException
	 */
	@AfterMethod
	public void getResult(ITestResult result) throws IOException {
		
		System.out.println("ITestResult: " + result);
		
//		if (result.getStatus() == ITestResult.FAILURE) {
//			test.log(Status.FAIL, MarkupHelper.createLabel("Test " + result.getName() + " ha fallado.", ExtentColor.RED));
//			test.fail(result.getThrowable());
//		} else if (result.getStatus() == ITestResult.SUCCESS) {
//			test.log(Status.INFO, MarkupHelper.createLabel("Test " + result.getName() + " se ha completado. ", ExtentColor.BLACK));
//		} else {
//			test.log(Status.WARNING, MarkupHelper.createLabel("Test " + result.getName() + " omitido ", ExtentColor.ORANGE));
//			test.warning(result.getThrowable());
//		}
		
		test.log(Status.INFO,
				MarkupHelper.createLabel("Test " + result.getName() + " se ha completado. ", ExtentColor.GREY));
		test.info("Ultima captura: ", MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
		
		setCategory();
		
		test.getModel().setStartTime(getTime(result.getStartMillis()));
        test.getModel().setEndTime(getTime(result.getEndMillis()));
	}
	
    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();      
    }

	public String returnScreenshot() {

		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
		Date fecha = new Date();

		String folder = "reportesHTML" + File.separator + formatoFecha.format(fecha) + File.separator + "screenshots";
		File capturaPantalla = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy__hh_mm_ss_SS");

		// Comprobar si la carpeta existe. Si no, se crea
		File folderScreenshots = new File(folder);

		if (!folderScreenshots.exists()) {
			if (!folderScreenshots.getParentFile().exists()) {
				folderScreenshots.getParentFile().mkdir();
			}
			folderScreenshots.mkdir();
		}

		String FechaHora = df.format(new Date());
		String destFile = System.getProperty("user.dir") + File.separator + "reportesHTML" + 
		File.separator +formatoFecha.format(fecha)+ File.separator + "screenshots" + File.separator + FechaHora + ".jpg";
		File target = new File(destFile);
		String destFileRelative = "screenshots" + File.separator + FechaHora + ".jpg";
		try {
			// FileUtils.copyFile(capturaPantalla, new File(folder + File.separator +
			// destFile));
			FileUtils.copyFile(capturaPantalla, target);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destFileRelative;
	}

	/**
	 * Método que finaliza la ejecución del driver y escribe el reporte.
	 */
	@AfterClass
	public void tearDown() {
		driver.close();
	}

	@AfterSuite
	public void genReport() {
		try {
			driver.quit();
			extent.flush();
			System.out.println("OK al desconectar");
			//closeTunnel();
		} catch (Exception ex) {
			System.out.println("Error al desconectar");
		} finally {
			log.registerFinal();
		}

	}
	
	public void setCategory() {
		
		nombreClase = this.getClass().getSimpleName();
		splitString = nombreClase.split("_");
		
		test.assignCategory(splitString[0]);
		
	}

}