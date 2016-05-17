import org.apache.commons.math.stat.regression.SimpleRegression;


public class DatosTempMin {

	private double tempSunSet;
	private double constA;
	private double constB;
	private double constC;
	public double humedad;
	private double puntoRocio;
	private double pronosticoTemMin;




	public void calcularPuntoRocio(double humedad, double temp) //calcula y muestra por pantalla el punto de rocio 
	{
		puntoRocio=Math.pow(humedad/100,1.0/8);

		puntoRocio=puntoRocio*(110+temp)-110;

		System.out.print("el punto de rocio es :"+puntoRocio+"\n");

	}


	public void setPuntoRocio(double puntoRocio)
	{

		this.puntoRocio=puntoRocio;

	}



	public double calcularPronosticoTempMin(int opt){ //devuelve y muestra por pantalla la prediccion de la temperatura min registrada en la noche  



		if(opt==0)		
			pronosticoTemMin= constA*tempSunSet+constB; // calculo sin punto de rocio

		else
			pronosticoTemMin= constA*tempSunSet+constC*puntoRocio+constB; // calculo con punto de rocio



		System.out.print("La temperatura min para esta noche sera:"+pronosticoTemMin+"\n");

		return pronosticoTemMin;

	}



	public double calcularIntercepcion(double x[],double y[]){ //calcula la intecepcion a partir de los arreglos de tempMin registrada en la noche y el T0  

		//y temMin
		//x T0


		SimpleRegression sr = new SimpleRegression();

		int i=0;
		while(x.length>i){
			// Add data
			sr.addData(x[i],y[i]);
			i++;
		}
		return sr.getIntercept();

	}

	public double calcularSlope(double x[],double y[]){ 

		//y temMin
		//x T0


		SimpleRegression sr = new SimpleRegression();

		int i=0;
		while(x.length>i){
			// Add data
			sr.addData(x[i],y[i]);
			i++;
		}
		return sr.getSlope();

	}



	public double getHumedad() {
		return humedad;
	}



	public void setHumedad(double humedad) {
		this.humedad = humedad;
	}



	public double getConstB() {
		return constB;
	}



	public void setConstB(float x[],float y[]) { // Le asigna un valor a la constant B calculando la intercepcion en entre los datos conocidos de tempMin y T0 

		//y temMin
		//x T0
		SimpleRegression sr = new SimpleRegression();

		int i=0;
		while(x.length>i){
			// Add data
			sr.addData(x[i],y[i]);
			i++;
		}
		constB=sr.getIntercept();

		System.out.print(constB+" \n");

		sr.clear();

	}



	public double getConstA() {
		return constA;
	}



	public void setConstA(float x[],float y[]) {// Le asigna un valor a la constant A calculando la pendiente en entre los datos conocidos de tempMin y T0

		SimpleRegression sr = new SimpleRegression();

		int i=0;
		while(x.length>i){
			// Add data
			sr.addData(x[i],y[i]);
			i++;
		}
		constA=sr.getSlope();

		System.out.print(constA+"\n");
		sr.clear();


	}



	public double getTempSunSet() {
		return tempSunSet;
	}



	public void setTempSunSet(double tempSunSet) {
		this.tempSunSet = tempSunSet;
	}


	public double getConstC() {
		return constC;
	}


	public void setConstC(float tempSet[] ,float tempRise[],float dewPoint[]) { // Le asigna un valor a la constant C calculando la pendiente en entre el punto de rocio y el error entre la prediccion de temp y el valor real medido  conocidos de tempMin

		
		float[] error  =new float[tempSet.length];

		int i=0;


		while(tempSet.length>i){


			error[i]=(float) (constA*tempSet[i]+constB);
			error[i]=tempRise[i]-error[i];
			i++;
		}



		SimpleRegression sr = new SimpleRegression();

		i=0;
		while(error.length>i){
			// Add data
			sr.addData(dewPoint[i],error[i]);
			i++;
		}
		constC=sr.getSlope();

		System.out.print(constC+"\n");
		sr.clear();

		// Y es el error


	}


}
