import java.sql.SQLException;
import java.text.ParseException;

public class Main {

	  public static void main(String[] args) throws SQLException, ParseException{
	

		  
		  
	//	InsertarTemp con= new InsertarTemp();
//		con.InsertarT0(6,21);

	//	con.InsertarTMin("07:00:00");

		float[] tempSet  =new float[50];
		float[] tempRise =new float[50];
		float[] dewPoint =new float[50];
	
		DatosTempMin datos= new DatosTempMin();
		
		InsertarDb ins= new InsertarDb();
		Prueba prueba=new Prueba();

		tempRise=ins.selectConst(1);	
		tempSet=ins.selectConst(0); //T0
		dewPoint=ins.selectConst(2);
		datos.setConstA(tempSet,tempRise);
		datos.setConstB(tempSet,tempRise);
		datos.setConstC(tempSet,tempRise,dewPoint);
		datos.setPuntoRocio(-2);
		datos.setTempSunSet(6);
		
		
	//	prueba.RealizarPrueba(6,datos);
		
		prueba.PruebaUnica("2015-7-08",6,-1,-2,datos);
	
	// prueba=new Prueba();
		
//	prueba.ExtraerContenido("6");
		
			
	 

	   
			

/*
	    datos.setTempSunSet(11);
	    datos.setPuntoRocio(-3.88);

		datos.calcularPronosticoTempMin(1);
*/
	//	InsertarTemp con= new InsertarTemp();
	//	con.InsertarT0(6,21);

	//	con.InsertarTMin("08:00:00");

	 
//	InsertarDb ins = new InsertarDb();	

	//ins.insertar("Historico","/home/norber/Documents/Facultad/Practicas/Eclipse/la.csv");
//	ins.insertar("Historico","/home/norber/Documents/Facultad/Practicas/Eclipse/DatosTemporada2003-2013mod.csv");

	  }


}
