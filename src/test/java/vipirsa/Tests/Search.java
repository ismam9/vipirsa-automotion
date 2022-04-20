package vipirsa.Tests;

import org.openqa.selenium.Keys;
import org.testng.annotations.Test;


import vipirsa.Config.WebActions;
import vipirsa.WebElements.PagWikipedia;

public class Search extends WebActions{
	
	@Test(description = "Search")
	public void vipirsa() throws Exception {
		test = extent.createTest("Search - Busqueda en Wikipedia");
		/*******************************************************************/
		PagWikipedia search = new PagWikipedia();
		/*******************************************************************/
		driver.get("https://www.google.com/");
		
		waitSecs(5);
		
		if (search.GOOGLECOOKIES.isDisplayed()) {
			System.out.println(search.GOOGLECOOKIES.getText());
			extentReportInfo("Ha saltado el modal para dar el consentimiento a Google");
			try {
				scrollTo(search.BTN_ACEPTO);
				waitSecs(3);
				click(search.BTN_ACEPTO);
				extentReportPass("Dado el consentimiento a Google correctamente");
			} catch (Exception e) {
				extentReportFail("Error al aceptar el consentimiento de Google");
			}
		}
		
		try {
			extentReportInfo("No ha saltado el modal para confirmar las cookies de Google");
			waitForVisibility(search.GOOGLELOGO);
			lookupElement(search.GOOGLELOGO);
			
			insertarCaptura();
			extentReportPass("Se ha cargado la URL 'https://www.google.com/' correctamente");
		} catch (Exception e) {

			extentReportFail("Error a cargar la página principal de Google");
		}
		
		try {
			waitForVisibility(search.GOOGLESEARCHBUTTON);
			lookupElement(search.GOOGLESEARCHBUTTON);
			click(search.GOOGLESEARCHBUTTON);
			sendKeys(search.GOOGLESEARCHBUTTON, "automatización");
			search.GOOGLESEARCHBUTTON.sendKeys(Keys.ENTER);
			//waitForVisibility(search.SPANELEMENTOPAGINATODO);
			
			insertarCaptura();
			extentReportPass("Se ha cargado la URL 'https://www.google.com/' correctamente");
		} catch (Exception e) {

			extentReportFail("Error al buscar en Google la palabra 'automatización'");
		}
		
		try {
			waitSecs(2);
			scrollTo(search.WIKIPAGE);
			waitSecs(2);
			click(search.WIKIPAGE);
			waitForVisibility(search.WIKILOGO);
			lookupElement(search.WIKILOGO);
			
			insertarCaptura();
			extentReportPass("Se ha visto un enlace de Wikipedia del resultado 'automatización' y se ha verificado la presencia en Wikipedia");
		} catch (Exception e) {

			extentReportFail("No se ha podido encontrar o cargar la página de Wikipidia");
		}
		
		try {
			scrollTo(search.PRIMERPROCESO);
			String anio = "1785";
			String texto = search.PRIMERPROCESO.getText();
			
			if (texto.contains(anio)) {
				extentReportInfo(search.PRIMERPROCESO.getText());
				extentReportPass("El primer proceso Automatizado fue en 1785, y vemos que SI está en Wikipedia");
			}
			
			insertarCaptura();
		} catch (Exception e) {

			extentReportFail("No se ha podido encontrar o cargar la página de Wikipidia");
		}

		extentReportPass("[OK] CAPTURA DE PANTALLA ACTUAL");
		
	}
}
