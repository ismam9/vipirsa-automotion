package vipirsa.WebElements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import vipirsa.Config.WebActions;

public class PagWikipedia extends WebActions {
	
	 public PagWikipedia() {
	        PageFactory.initElements(driver, this);
	 }  
	 
	 @FindBy(xpath = "//img[@alt=\"Google\"]")
	 public WebElement GOOGLELOGO;
	 
	 @FindBy(xpath = "//input[@title=\"Buscar\"]")
	 public WebElement GOOGLESEARCHBUTTON;
	 
	 @FindBy(xpath = "//div[@aria-current=\"page\"]")
	 public WebElement PAGETODO;
	 
	 public String SPANELEMENTOPAGINATODO = "//span[text()='Todo']";
	 
	 @FindBy(xpath = "//cite[text()='https://es.wikipedia.org']")
	 public WebElement WIKIPAGE;
	 
	 @FindBy(xpath = "//a[@href=\"/wiki/Wikipedia:Portada\"]")
	 public WebElement WIKILOGO;
	 
	 @FindBy(xpath = "//*[@id=\"mw-content-text\"]/div[1]/p[32]")
	 public WebElement PRIMERPROCESO;
	 
	 @FindBy(xpath = "//*[@id=\"S3BnEe\"]")
	 public WebElement GOOGLECOOKIES;
	 
	 public String STRING_GOOGLECOOKIES_TEXTO = "//h1[text()='Antes de ir a la Búsqueda de Google']";

	 @FindBy(xpath = "//div[contains(text(),'Acepto')]")
	 public WebElement BTN_ACEPTO;

	 
	 
}
