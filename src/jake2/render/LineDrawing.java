package jake2.render;

public interface LineDrawing {
	public void beginPath();
	public void clearRect(float x, float y, float w, float h);
	public void fillText(String text, float x, float y);
	public void lineTo(float x, float y);
	public void moveTo(float x, float y);
	public void stroke();
	public void setGlobalAlpha(float ga);
	public void setStrokeStyleColor(String strokeStyle); 
	
	public interface SwapBuffersCallback {
		LineDrawing glSwapBuffers();
	}
}
