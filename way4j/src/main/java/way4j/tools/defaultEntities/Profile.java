package way4j.tools.defaultEntities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class Profile {

		@Id
		private Long idProfile;
		
		@OneToMany
		private List<Permission> permissions;
		
		@ManyToOne
		private User user;

		public Long getIdProfile() {
			return idProfile;
		}

		public void setIdProfile(Long idProfile) {
			this.idProfile = idProfile;
		}

		public List<Permission> getPermissions() {
			return permissions;
		}

		public void setPermissions(List<Permission> permissions) {
			this.permissions = permissions;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}
		
}
