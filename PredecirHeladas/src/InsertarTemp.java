import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


public class InsertarTemp {

	public Date fecha;
	public float temp0;
	public float tempM;
	public float PuntoRocio;


	public void InsertarT0(int mes,int t0){ // Insertar en la tabla TempMin (datos con condiciones optimas para realizar prediccion) los valores de T0

		Conectar conexion = new Conectar();
		conexion.DbConnection();

		java.sql.Statement st=null;
		try {
			st = conexion.conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			ResultSet rs = st.executeQuery("SELECT * FROM HistoricoFree WHERE CoberturaCielo='limpio' and MONTH(FechaHora)='"+mes+"' and HOUR(FechaHora)>="+t0+" and HOUR(FechaHora)<"+(t0+1));

			int i=0;

			String query="INSERT INTO TempMin (Nodo,Fecha,Temp0,TempRise,PuntoRocio) VALUES ";

			while (rs.next())
			{
				String date;

				if(i!=0)
				{
					query=query+",";

				}

				fecha=(Date) rs.getObject("FechaHora");	
				temp0=(float) rs.getObject("Temperatura");
				PuntoRocio=(float)rs.getObject("PuntoRocio");
				date=fecha.toString();
				String token[] = date.split(" ");
				date=token[0];

				query=query+"('1','"+date+"','"+temp0+"',NULL,'"+PuntoRocio+"')";
				i=1;

			}

			System.out.print(query); 
			PreparedStatement stmt=null;
			conexion.conn.createStatement();
			stmt= conexion.conn.prepareStatement(query);
			stmt.execute();
			stmt.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		conexion.desconectar();


	}


	void InsertarTMin(String tRise){


		Conectar conexion = new Conectar();
		conexion.DbConnection();


		Conectar conexion2 = new Conectar();
		conexion2.DbConnection();

		java.sql.Statement st=null;
		java.sql.Statement st2=null;

		try {
			st = conexion.conn.createStatement();
			st2 = conexion2.conn.createStatement();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			ResultSet rs = st.executeQuery("select Fecha from TempMin WHERE TempRise IS NULL");


			String query="";

			while (rs.next())
			{

				fecha=(Date) rs.getObject("Fecha");
				System.out.print("\n"+fecha+"\n");

				ResultSet rs2 = st2.executeQuery("select MIN(temperatura) from HistoricoFree WHERE FechaHora>ADDDATE('"+fecha+" 04:00:00',1) and FechaHora<=ADDDATE('"+fecha+" "+tRise+"',1)");
				rs2.next();
				if (rs2.getObject(1) == null){
					continue;
				}
				tempM=(float) rs2.getObject(1);
				query="UPDATE `TempMin` SET `TempRise`='"+tempM+"' WHERE Fecha='"+fecha+"'";
				System.out.print(query);
				PreparedStatement stmt=null;
				conexion.conn.createStatement();
				stmt= conexion.conn.prepareStatement(query);
				stmt.execute();
				stmt.close();

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print("explootaaaaa");
			e.printStackTrace();
		}

		conexion.desconectar();

	}

}
