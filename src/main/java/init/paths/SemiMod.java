package init.paths;

import snake2d.util.sets.LIST;

import java.nio.file.Path;

final class SemiMod extends PATH {
	
	private final init.paths.VirtualFolder f;
	
	SemiMod(String path, String filetype) {
		super(filetype);
		f = new init.paths.VirtualFolder(PATHS.i.paths, path);
	}
	
	SemiMod(LIST<Path> roots, String path, String filetype) {
		super(filetype);
		f = new init.paths.VirtualFolder(roots, path);
	}
	
	SemiMod(init.paths.VirtualFolder f, String filetype) {
		super(filetype);
		this.f = f;
	}

	@Override
	public String[] getFiles() {
		return f.listFiles(filetype);
	}
	
	@Override
	public String[] getFilesOrdered() {
		return f.listFilesOrdered(filetype);
	}
	
	@Override
	public String[] folders() {
		return f.listFolders();
	}

	@Override
	protected Path getRaw(CharSequence resource) {
		return f.getExistingFile(resource);
	}

	@Override
	protected LIST<Path> getAllRaw(CharSequence resource) {
		return f.getPossibleFiles(resource);
	}

	@Override
	protected void validate() {
		
	}
	
	@Override
	protected PATH getFolder(CharSequence folder, String filetype, boolean create) {
		return new SemiMod(f.folder(folder), filetype);
	}

	@Override
	public boolean exists(CharSequence file) {
		return f.exists(file, filetype);
	}

	@Override
	public Path get() {
		return f.getExistingFile(null);
	}
	
	@Override
	public boolean existsFolder(CharSequence folder) {
		return f.exists(folder, "");
	}
	
	@Override
	public boolean exists(CharSequence file, CharSequence fileType) {
		return f.exists(file, filetype);
	}

}