package postpc.musica;

public class youTubeEntery {

	public youTubeEntery(String title, String tumb, String videoId) {
		this.title = title;
		this.tumb = tumb;
		String[] parts = videoId.split("/");
		this.videoId = parts[parts.length - 1];
	}

	public String videoId;
	public String title;
	public String tumb;
}
