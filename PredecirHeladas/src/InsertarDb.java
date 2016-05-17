
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.csvreader.CsvReader;

public class InsertarDb {

	private	String pathCsv;
	private	String tabla;

	public void insertar(String tabla , String pathCsv) throws SQLException{ //copia los datos del archivo csv a una tambla de la base de datos predeterminada
		this.setPathCsv(pathCsv);
		this.setTabla(tabla);


		Conectar conexion = new Conectar();
		conexion.DbConnection();
		CsvReader reader = null;
		try {
			reader = new CsvReader(pathCsv);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(tabla=="Datos"){
			int i=0;
			String query="INSERT INTO Datos (idNodo,FechaHora,Temp,Humedad) VALUES ";
			try {
				while (reader.readRecord())
				{

					if(i!=0)
					{
						query= query+",";
					}

					String id = reader.get(0);
					String hora = reader.get(1);
					String minutos = reader.get(2);
					String segundos = reader.get(3);
					String dia = reader.get(4);
					String mes = reader.get(5);
					String año = reader.get(6);
					String temp= reader.get(7);
					String humedad= reader.get(8);

					query = query+ "('"+id+"','"+año+"/"+mes+"/"+dia+" "+hora+":"+minutos+":"+segundos+"','"+temp+"','"+humedad+"')";
					i=1;

				}

				System.out.print(query);
				PreparedStatement stmt=null;
				conexion.conn.createStatement();
				stmt= conexion.conn.prepareStatement(query);
				stmt.execute();
				stmt.close();



			}catch (IOException e) {
				e.printStackTrace();
			}


		}



		if(tabla=="Crepusculo"){


			int i=0;
			String query="INSERT INTO Crepusculo (Fecha,sunSet,sunRise,Time0) VALUES ";
			try {
				while (reader.readRecord())
				{
					if(i!=0)
					{
						query= query+",";
					}
					String dia = reader.get(0);
					String mes = reader.get(1);
					String año = reader.get(2);
					String sunRise= reader.get(3);
					String sunSet= reader.get(4);
					String delims = "[:]";
					String[] tokens = sunSet.split(delims);
					int t0 = Integer.parseInt(tokens[0]);
					t0=t0+2;
					query = query+ "('"+año+"/"+mes+"/"+dia+"','"+sunSet+"','"+sunRise+"','"+t0+":"+tokens[1]+"')";
					i=1;

				}

				System.out.print(query);
				PreparedStatement stmt=null;
				conexion.conn.createStatement();
				stmt= conexion.conn.prepareStatement(query);
				stmt.execute();
				stmt.close();	

			}catch (IOException e) {
				e.printStackTrace();
			}

		}


		if(tabla=="Historico"){

			System.out.print("entro\n");

			int i=0,j=0;
			String query="INSERT INTO Historico (FechaHora,Temperatura,PuntoRocio,CoberturaCielo) VALUES ";
			try {

				reader.setDelimiter(';');


				while (reader.readRecord())
				{

					if (i==0)

						query="INSERT INTO Historico (FechaHora,Temperatura,PuntoRocio,CoberturaCielo) VALUES ";



					String año = reader.get(0);
					String mes = reader.get(1);
					String dia = reader.get(2);
					String hora= reader.get(3);
					String minutos= reader.get(4);
					String cielo= reader.get(5);
					String temp= reader.get(6);
					String punto= reader.get(7);
					System.out.print(punto+"\n");
					if(punto.equals("null"))
						punto=null;
					if(temp.equals("null"))
						temp=null;

					if(cielo.equals("CLR") ){
						cielo="limpio";

					}
					else if(cielo.equals("SCT")){

						cielo="disperso";


					}
					else if(cielo.equals("BKN")){

						cielo="semiCubierto";

					}

					else if(cielo.equals("OVC")){

						cielo="cubierto";

					}

					else{
						cielo=null;
					}

					if(cielo==null || punto==null || temp==null)
						continue;



					else{
						if(j==0)
							query= query+"('"+año+"/"+mes+"/"+dia+" "+hora+":"+minutos+":00','"+temp+"','"+punto+"','"+cielo+"')";	

						else	
							query= query+",('"+año+"/"+mes+"/"+dia+" "+hora+":"+minutos+":00','"+temp+"','"+punto+"','"+cielo+"')";

					}
					System.out.print(i+"\n");
					i++;
					j=1;

					if(i==400){
						i=0;
						query=query+";";
						System.out.print(query+"\n");
						//query=query+";";
						PreparedStatement stmt=null;
						conexion.conn.createStatement();
						stmt= conexion.conn.prepareStatement(query);
						stmt.execute();
						stmt.close();
						query="";
						j=0;

					}


				}
				query=query+";  ";
				System.out.print(query);
				PreparedStatement stmt=null;
				conexion.conn.createStatement();
				stmt= conexion.conn.prepareStatement(query);
				stmt.execute();
				stmt.close();	

			}catch (IOException e) {
				e.printStackTrace();
			}

		}



		conexion.conn.close();

	}

	public float[] selectConst(int opt){ // busca en la tabla TempMin y devuelve de acuerdo a una opt un arreglo de T0 , PuntoDeRocio o TempRise 


		float[] datos = new float[50];
		float[] datos2 = new float[50];
		float[] datos3=new float[50];



		Conectar conexion = new Conectar();
		conexion.DbConnection();


		java.sql.Statement st=null;

		try {
			st = conexion.conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// T0 opt=0

		System.out.print("entrooo\n");


		try {
			ResultSet rs = st.executeQuery("select Temp0,TempRise,PuntoRocio from TempMin LIMIT 50 ");

			int i=0;

			while (rs.next())
			{

				datos[i]=(float) rs.getObject("Temp0");
				datos2[i]=(float) rs.getObject("TempRise");
				datos3[i]=(float) rs.getObject("PuntoRocio");
				System.out.print(datos[i]+" "+datos2[i]+" "+datos3[i]+"\n");
				i++;
			}

			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


		conexion.desconectar(); 

		if(opt==1)
			return datos2;

		else if(opt==0)
			return datos;

		else 
			return datos3;

	} 



	public String getPathCsv() {
		return pathCsv;
	}


	public void setPathCsv(String pathCsv) {
		this.pathCsv = pathCsv;
	}


	public String getTabla() {
		return tabla;
	}

	public void setTabla(String tabla) {
		this.tabla = tabla;
	}


}
