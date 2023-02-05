import io.inverno.core.annotation.Module;

@Module
module org.example.weather {
	requires io.inverno.core;
	requires jsr305;
	requires minimal.json;
}
