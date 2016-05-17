import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Prueba {

	public Date fecha;
	public float t0;
	public float tMin;
	public float predTMin;
	public float predTMinDewPoint;


	void InsertPrueba (Date fecha ,float t0 ,float tMin , float dewPoint, DatosTempMin datos) throws SQLException{

		String query;
		float error1;
		float error2;
		this.fecha=fecha;
		this.t0=t0;
		this.tMin=tMin;
		Conectar conexion = new Conectar();
		conexion.DbConnection();
		datos.setTempSunSet(t0);
		datos.setPuntoRocio(dewPoint);
		predTMin=(float) datos.calcularPronosticoTempMin(0);
		predTMinDewPoint=(float) datos.calcularPronosticoTempMin(1);
		error1=Math.abs((tMin-predTMin));
		error2=Math.abs((tMin-predTMinDewPoint));

		query="INSERT INTO Pruebas(Fecha,T0,TMin,PredTMin,PredTMinDewPoint,Error1,Error2) VALUES ('"+this.fecha+"','"+this.t0+"','"+tMin+"','"+predTMin+"','"+predTMinDewPoint+"','"+error1+"','"+error2+"')"; 	
		System.out.print(query);
		PreparedStatement stmt=null;
		conexion.conn.createStatement();
		stmt= conexion.conn.prepareStatement(query);
		stmt.execute();
		stmt.close();

	}

	void RealizarPrueba (int mes,DatosTempMin datos){

		Date fecha;
		float t0;
		float tMin;
		float dewPoint;

		Conectar conexion = new Conectar();
		conexion.DbConnection();


		java.sql.Statement st=null;

		try {
			st = conexion.conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			ResultSet rs = st.executeQuery("select * from TempMin where MONTH(fecha)='"+mes+"' LIMIT 50 ");


			while (rs.next())
			{

				fecha=(Date)rs.getObject("fecha");
				t0=(float) rs.getObject("Temp0");
				tMin=(float) rs.getObject("TempRise");
				dewPoint=(float) rs.getObject("PuntoRocio");

				System.out.print("T0="+t0+" Tmin="+tMin+" fecha="+fecha+"Punto="+dewPoint+"\n");
				InsertPrueba(fecha,t0,tMin,dewPoint,datos);

			}
			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	void PruebaUnica(String fecha ,float t0 ,float tMin , float dewPoint, DatosTempMin datos) throws SQLException{

		String query;
		float error1;
		float error2;
		this.t0=t0;
		this.tMin=tMin;
		Conectar conexion = new Conectar();
		conexion.DbConnection();
		datos.setTempSunSet(t0);
		datos.setPuntoRocio(dewPoint);
		predTMin=(float) datos.calcularPronosticoTempMin(0);
		predTMinDewPoint=(float) datos.calcularPronosticoTempMin(1);
		error1=Math.abs((tMin-predTMin));
		error2=Math.abs((tMin-predTMinDewPoint));

		query="INSERT INTO Pruebas(Fecha,T0,TMin,PredTMin,PredTMinDewPoint,Error1,Error2) VALUES ('"+fecha+"','"+this.t0+"','"+tMin+"','"+predTMin+"','"+predTMinDewPoint+"','"+error1+"','"+error2+"')"; 	
		System.out.print("Fecha:"+fecha+"   TMin:"+tMin+"\nPrediccion sin punto de rocio:"+predTMin+"\nPrediccion con punto de rocio:"+predTMinDewPoint+"\nError1:"+error1+" Error2:"+error2);

		System.out.print("\n"+query);


		PreparedStatement stmt=null;
		conexion.conn.createStatement();
		stmt= conexion.conn.prepareStatement(query);
		stmt.execute();
		stmt.close();

	}
	void ExtraerContenido(String mes ) throws SQLException {

		Document doc;
		int i=0,c=0,j=0,i2=0,cant,año=0,mes2;
		String [] hora= new String [24];
		String [] dewPoint= new String [24];
		String [] temperatura=new String [24];
		String [] cielo= new String [24];
		Date date2= new Date();
		String query = null,url = null,numero;


		System.out.println("la long es:"+hora.length);

		SimpleDateFormat formateador2 =	new	SimpleDateFormat("yyyy-MM-dd");
		try {
			date2=formateador2.parse("2010-"+mes+"-1");


		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar cal2 = new GregorianCalendar();
		cal2.setLenient(false);
		cal2.setTime(date2);

		
		
		Conectar conexion = new Conectar();
		conexion.DbConnection();

		System.out.print("date2="+formateador2.format(date2));

		mes2=Integer.parseInt(mes);
		
		
		switch(mes2){
		case 1:  // Enero
		case 3:  // Marzo
		case 5:  // Mayo
		case 7:  // Julio
		case 8:  // Agosto
		case 10:  // Octubre
		case 12: // Diciembre
		mes2=31;	
		break;
		case 2:
		case 4:  // Abril
		case 6:  // Junio
		case 9:  // Septiembre
		case 11: // Noviembre
		mes2=30;
		break;
		}		
		
		
		
		
		for(c=0,i=0;c<mes2;c++)
		{
			url="http://freemeteo.ar.com/eltiempo/mendoza/historia/historial-diario/?gid=3844421&station=23812&date="+formateador2.format(date2)+"&language=spanishar&country=argentina";
			System.out.println("\n"+url);

			try {

				doc = Jsoup.connect(url).get();
			//	System.out.println("la url es: "+url);
				//System.out.println("entro al 1er elemen:"+doc.html());

				i=0;
				Elements lineks = doc.select("table.daily-history");
				for (Element linek : lineks) {

					Elements datos=linek.select("tbody");
					for(Element dato : datos){

						Elements datos4 = dato.select("td.white.no-top");
						for(Element dato4 : datos4){
							hora[i]=dato4.text();
			//				System.out.println("hora:" + hora[i]);	
							i++;
							if(i>23)
								break;
							
						}
						i=0;

						Elements datos5 = dato.select("td.tl script");

						for(Element dato5 :datos5)
						{						
							numero=dato5.html();
							numero = numero.substring(numero.lastIndexOf("(")+1,numero.lastIndexOf(","));
		//					System.out.println("numero: " + numero);
							//var Icons = {"Forecast":{"1":{"Description":"Buen tiempo","ShortDescription":"Despejado"},"2":{"Description":"Pocas nubes","ShortDescription":"Pocas nubes"},"3":{"Description":"Cielos parcialmente cubiertos","ShortDescription":"Parcialmente cubierto"},"4":{"Description":"Cielos cubiertos","ShortDescription":"Cubierto"},"5":{"Description":"Probabilidad de lluvia","ShortDescription":"Lluvia ligera"},"6":{"Description":"Lluvia ligera","ShortDescription":"Lluvia ligera"},"7":{"Description":"Lluvia","ShortDescription":"Lluvia"},"8":{"Description":"Lluvia intensa","ShortDescription":"Lluvia intensa"},"9":{"Description":"Probabilidad de truenos","ShortDescription":"Truenos"},"10":{"Description":"Lluvia y probabilidad de truenos","ShortDescription":"Truenos"},"11":{"Description":"Lluvia y probabilidad de tormentas fuertes","ShortDescription":"Truenos"},"12":{"Description":"Probabilidad de lluvia o aguanieve","ShortDescription":"Lluvia o aguanieve"},"13":{"Description":"Lluvia ligera o aguanieve","ShortDescription":"Lluvia o aguanieve"},"14":{"Description":"Lluvia o aguanieve","ShortDescription":"Lluvia o aguanieve"},"15":{"Description":"Lluvia intensa o aguanieve","ShortDescription":"Lluvia o aguanieve"},"16":{"Description":"Lluvia o aguanieve y probabilidad de truenos","ShortDescription":"Lluvia o aguanieve"},"17":{"Description":"Lluvia o aguanieve y probabilidad de fuertes truenos","ShortDescription":"Lluvia o aguanieve"},"18":{"Description":"Probabilidad de aguanieve o nieve","ShortDescription":"Aguanieve o nieve"},"19":{"Description":"Aguanieve ligera o nieve","ShortDescription":"Aguanieve o nieve"},"20":{"Description":"Aguanieve o nieve","ShortDescription":"Aguanieve o nieve"},"21":{"Description":"Aguanieve intensa o nieve","ShortDescription":"Aguanieve o nieve"},"22":{"Description":"Aguanieve o nieve y probabilidad de truenos","ShortDescription":"Aguanieve o nieve"},"23":{"Description":"Aguanieve o nieve y probabilidad de fuertes truenos","ShortDescription":"Aguanieve o nieve"},"24":{"Description":"Probabilidad de nieve","ShortDescription":"Nieve ligera"},"25":{"Description":"Nieve ligera","ShortDescription":"Nieve ligera"},"26":{"Description":"Nieve","ShortDescription":"Nieve"},"27":{"Description":"Nieve intensa","ShortDescription":"Nieve intensa"},"28":{"Description":"Nieve y probabilidad de truenos","ShortDescription":"Nieve intensa"},"29":{"Description":"Nieve y probabilidad de fuertes truenos","ShortDescription":"Nieve intensa"},"30":{"Description":"Buen tiempo","ShortDescription":""},"31":{"Description":"Mayormente despejado","ShortDescription":""},"32":{"Description":"Cielos parcialmente cubiertos","ShortDescription":""},"33":{"Description":"Cielos cubiertos","ShortDescription":""},"34":{"Description":"Mayormente despejado con probabilidad de lluvia","ShortDescription":""},"35":{"Description":"Parcialmente cubierto con probabilidad de lluvia","ShortDescription":""},"36":{"Description":"Cubierto con probabilidad de chaparrones","ShortDescription":""},"37":{"Description":"Lluvia ligera con intervalos de sol","ShortDescription":""},"38":{"Description":"Cubierto con chubascos ligeros","ShortDescription":""},"39":{"Description":"Lluvia con intervalos de sol","ShortDescription":""},"40":{"Description":"Cubierto con lluvias intermitentes","ShortDescription":""},"41":{"Description":"Chubascos intensos con intervalos de cielo despejado","ShortDescription":""},"42":{"Description":"Cubierto con lluvias intermitentes intensas ","ShortDescription":""},"43":{"Description":"Chubascos ligeros","ShortDescription":""},"44":{"Description":"Chubascos","ShortDescription":""},"45":{"Description":"Intervalos de cielo despejado y lluvia con probabilidad de truenos","ShortDescription":""},"46":{"Description":"Intervalos de cielo despejado y lluvia con probabilidad de fuertes truenos","ShortDescription":""},"47":{"Description":"Cubierto y lluvioso con probabilidad de truenos","ShortDescription":""},"48":{"Description":"Cubierto y lluvioso con probabilidad de tormentas fuertes","ShortDescription":""},"49":{"Description":"Mayormente despejado con probabilidad de lluvia o chubascos de aguanieve","ShortDescription":""},"50":{"Description":"Parcialmente nublado con probabilidad de lluvia o chubascos de aguanieve","ShortDescription":""},"51":{"Description":"Cubierto con probabilidad de lluvia o chubascos de aguanieve","ShortDescription":""},"52":{"Description":"Lluvias ligeras o chubascos de aguanieve con intervalos de sol ","ShortDescription":""},"53":{"Description":"Cubierto con lluvias ligeras o chubascos de aguanieve","ShortDescription":""},"54":{"Description":"Lluvias o chubascos de aguanieve con intervalos de sol","ShortDescription":""},"55":{"Description":"Cubierto con lluvias o chubascos de aguanieve","ShortDescription":""},"56":{"Description":"Lluvias intensas o chubascos de aguanieve con intervalos de sol","ShortDescription":""},"57":{"Description":"Cubierto con lluvias intermitentes intensas o chubascos de aguanieve","ShortDescription":""},"58":{"Description":"Lluvias ligeras o chubascos de aguanieve","ShortDescription":""},"59":{"Description":"Lluvia o chubascos de aguanieve","ShortDescription":""},"60":{"Description":"Períodos despejados y lluvias o chubascos de aguanieve con probabilidad de truenos","ShortDescription":""},"61":{"Description":"Intervalos de cielo despejado y chubascos de aguanieve con probabilidad de fuertes truenos","ShortDescription":""},"62":{"Description":"Cubierto con lluvia o chubascos de aguanieve y probabilidad de truenos","ShortDescription":""},"63":{"Description":"Cubierto con lluvia o chubascos de aguanieve y probabilidad de fuertes truenos","ShortDescription":""},"64":{"Description":"Mayormente despejado con probabilidad de aguanieve o chubascos de nieve ","ShortDescription":""},"65":{"Description":"Parcialmente cubierto con probabilidad de aguanieve o chubascos de nieve","ShortDescription":""},"66":{"Description":"Cubierto con probabilidad de aguanieve o chubascos de nieve ","ShortDescription":""},"67":{"Description":"Aguanieve ligera o chubascos de nieve con intervalos de sol","ShortDescription":""},"68":{"Description":"Cubierto con aguanieve ligera o chubascos de nieve","ShortDescription":""},"69":{"Description":"Aguanieve o chubascos de nieve con intervalos de sol","ShortDescription":""},"70":{"Description":"Cubierto con aguanieve o chubascos de nieve","ShortDescription":""},"71":{"Description":"Aguanieve intensa o chubascos de nieve con intervalos de sol","ShortDescription":""},"72":{"Description":"Cubierto con períodos de aguanieve intensa o chubascos de nieve","ShortDescription":""},"73":{"Description":"Aguanieve ligera o chubascos de nieve","ShortDescription":""},"74":{"Description":"Aguanieve o chubascos de nieve","ShortDescription":""},"75":{"Description":"Intervalos de cielo despejado y aguanieve o nieve con probabilidad de truenos","ShortDescription":""},"76":{"Description":"Intervalos de cielo despejado y aguanieve o nieve con probabilidad de fuertes truenos","ShortDescription":""},"77":{"Description":"Cubierto con aguanieve o chubascos de nieve y probabilidad de truenos","ShortDescription":""},"78":{"Description":"Cubierto con aguanieve o chubascos de nieve y probabilidad de fuertes truenos ","ShortDescription":""},"79":{"Description":"Mayormente despejado con probabilidad de chubascos de nieve","ShortDescription":""},"80":{"Description":"Parcialmente cubierto con probabilidad de chubascos de nieve","ShortDescription":""},"81":{"Description":"Cubierto con probabilidad de chubascos de nieve","ShortDescription":""},"82":{"Description":"Chubascos de nieve ligeros con intervalos de sol","ShortDescription":""},"83":{"Description":"Cubierto con chubascos de nieve ligeros","ShortDescription":""},"84":{"Description":"Chubascos de nieve con intervalos de sol","ShortDescription":""},"85":{"Description":"Cubierto con chubascos de nieve","ShortDescription":""},"86":{"Description":"Chubascos fuertes de nieve con intervalos de sol","ShortDescription":""},"87":{"Description":"Cubierto con intervalos de chubascos fuertes de nieve ","ShortDescription":""},"88":{"Description":"Chubascos de nieve ligeros","ShortDescription":""},"89":{"Description":"Chubascos de nieve","ShortDescription":""},"90":{"Description":"Intervalos de cielo despejado y chubascos de nieve con probabilidad de truenos","ShortDescription":""},"91":{"Description":"Intervalos de cielo despejado y chubascos de nieve con probabilidad de fuertes truenos","ShortDescription":""},"92":{"Description":"Cubierto con chubascos de nieve y probabilidad de truenos","ShortDescription":""},"93":{"Description":"Cubierto con chubascos de nieve y probabilidad de fuertes truenos","ShortDescription":""},"94":{"Description":"Niebla helada","ShortDescription":"Niebla helada"}},"CurrentWeather":{"1":{"Description":"Buen tiempo"},"2":{"Description":"Pocas nubes"},"3":{"Description":"Cielos parcialmente cubiertos"},"4":{"Description":"Cielos cubiertos"},"7":{"Description":"Lluvia"},"10":{"Description":"Tormenta"},"26":{"Description":"Nieve"},"28":{"Description":"Tormenta de nieve"}},"Estimated":{"1":{"Description":"Despejado"},"2":{"Description":"Pocas nubes"},"3":{"Description":"Parcialmente cubierto"},"4":{"Description":"Cubierto"},"5":{"Description":"Lluvia "},"6":{"Description":"Lluvia"},"7":{"Description":"Lluvia"},"8":{"Description":"Lluvia "},"9":{"Description":"Lluvia"},"10":{"Description":"Lluvia"},"11":{"Description":"Lluvia"},"12":{"Description":"Aguanieve"},"13":{"Description":"Aguanieve"},"14":{"Description":"Aguanieve"},"15":{"Description":"Aguanieve"},"16":{"Description":"Aguanieve"},"17":{"Description":"Aguanieve"},"18":{"Description":"Aguanieve"},"19":{"Description":"Aguanieve"},"20":{"Description":"Aguanieve"},"21":{"Description":"Aguanieve"},"22":{"Description":"Aguanieve"},"23":{"Description":"Aguanieve"},"24":{"Description":"Nieve"},"25":{"Description":"Nieve"},"26":{"Description":"Nieve"},"27":{"Description":"Nieve"},"28":{"Description":"Nieve"},"29":{"Description":"Nieve"}}};Icons.GetDescription = function(id,key){ key = key || 'Forecast'; var o = this[key][id] || this['Forecast'][id]; if(!o) return ''; return o.Description || this['Forecast'][id].Description;};Icons.GetShortDescription = function(id,key){ key = key || 'Forecast'; var o = this[key][id] || this['Forecast'][id]; if(!o) return ''; return o.ShortDescription || this['Forecast'][id].ShortDescription;};

							if(numero.equals("1") ){
								cielo[i]="limpio";

							}
							else if(numero.equals("2")){

								cielo[i]="disperso";

							}
							else if(numero.equals("3")){

								cielo[i]="semiCubierto";
							}

							else
								cielo[i]="cubierto";

							i++;
							if(i>23)
								break;
						}

						i=0;
						i2=0;
						j=0;
						Elements datos2=dato.select("td");
						for(Element dato2 : datos2){

							if(i2==6){
								String temp=dato2.text();
								String[] tokens = temp.split("[°]+");
								dewPoint[j]=tokens[0];
	//							System.out.println("Punto Rocio:"+dewPoint[j]+" °");
								j++;
								if(j>23)
									break;
							}
							else if(i2==10)
								i2=0;
							Elements datos3=dato2.select("b");
							for(Element dato3 : datos3){

								String temp=dato3.text();
								String[] tokens = temp.split("[°]+");
								temperatura[i]=tokens[0];
//								System.out.println("Temperatura:" + temperatura[i]+"°c");
								i++;
								if(i>23)
									break;
							}
							i2++;
						}	
					}
				}


				for(i=0,cant=0;i<hora.length-1;i++){

					if(!hora[i].equals(" "))
						cant++;
				}

				for(i=0;i<cant-1;i++){

					if(i==0)
						query="INSERT INTO HistoricoFree (FechaHora,Temperatura,PuntoRocio,CoberturaCielo) VALUES ('"+formateador2.format(date2)+" "+hora[i]+":00','"+temperatura[i]+"',"+dewPoint[i]+",'"+cielo[i]+"')";		

					else
						query= query+" , ('"+formateador2.format(date2)+" "+hora[i]+":00','"+temperatura[i]+"',"+dewPoint[i]+",'"+cielo[i]+"')";		
				}


				System.out.println(query);
				PreparedStatement stmt=null;
				try{
				conexion.conn.createStatement();
				stmt= conexion.conn.prepareStatement(query);
				
				
				stmt.execute();
				stmt.close();
				query="";
				
				for(i=0;i<hora.length-1;i++){
					hora[i]=" ";	
					temperatura[i]=" ";
					cielo[i]=" ";
					dewPoint[i]=" ";
				}

				//cal2.add(Calendar.DAY_OF_MONTH,1);
				//date2=cal2.getTime();
				if(c==((mes2)-1) && año< 5){
				
					año++;
					c=-1;
					cal2.add(Calendar.DAY_OF_MONTH,1);
					cal2.add(Calendar.YEAR,1);
					cal2.add(Calendar.MONTH,-1);
					date2=cal2.getTime();
					
					System.out.println("c="+c);

				}
				else{
					cal2.add(Calendar.DAY_OF_MONTH,1);
					date2=cal2.getTime();
					System.out.println("c="+c);

				}

			}catch (SQLException e){
	
				System.out.print("error Mysql en la consulta");
				e.printStackTrace();
			}

			} catch (IOException e) {
				System.out.println("No se puede acceder a la pag :(");
				e.printStackTrace();
			}

		}

	}


}
